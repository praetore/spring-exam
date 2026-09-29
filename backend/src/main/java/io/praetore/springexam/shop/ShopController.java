package io.praetore.springexam.shop;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mini-webshop om bean scopes in een herkenbare flow te voelen (geen abstracte id-vergelijker).
 *
 * <p><b>Welke scope doet wat?</b>
 * <ul>
 *   <li>{@link InventoryService} — singleton: gedeelde voorraad</li>
 *   <li>{@link ShoppingCart} — session: mandje blijft hangen zolang de sessie-cookie leeft</li>
 *   <li>{@link RequestAudit} — request: nieuw requestId per HTTP-call</li>
 *   <li>{@link CheckoutDraft} — prototype via {@link ObjectProvider}: verse draft per checkout</li>
 *   <li>{@link StorefrontStats} — application: app-brede hit-teller</li>
 * </ul>
 *
 * <p><b>Probeer (HTTP Client: {@code backend/http/shop.http}):</b>
 * <ol>
 *   <li>GET /api/shop — voorraad + hits</li>
 *   <li>POST /api/shop/cart — items toevoegen</li>
 *   <li>GET /api/shop/cart — mandje nog gevuld (zelfde sessie)</li>
 *   <li>POST /api/shop/checkout — stock daalt, nieuwe draftId</li>
 *   <li>Cookies wissen / nieuw bestand → lege cart (nieuwe sessie)</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final InventoryService inventory;
    private final ShoppingCart cart;
    private final RequestAudit audit;
    private final StorefrontStats stats;
    /** ObjectProvider = elke {@code getObject()} levert een nieuwe prototype-bean. */
    private final ObjectProvider<CheckoutDraft> drafts;

    public ShopController(
            InventoryService inventory,
            ShoppingCart cart,
            RequestAudit audit,
            StorefrontStats stats,
            ObjectProvider<CheckoutDraft> drafts
    ) {
        this.inventory = inventory;
        this.cart = cart;
        this.audit = audit;
        this.stats = stats;
        this.drafts = drafts;
    }

    @GetMapping
    public Map<String, Object> storefront(HttpServletRequest request) {
        audit.bind(request);
        int hits = stats.hit();
        return Map.of(
                "welcome", "Spring Exam Merch",
                "stock", inventory.stock(),
                "storefrontHits", hits,
                "request", auditView(),
                "howTo", List.of(
                        "POST /api/shop/cart met {\"product\":\"spring-mug\",\"qty\":1}",
                        "GET  /api/shop/cart  (zelfde sessie = gevuld mandje)",
                        "POST /api/shop/checkout met {\"email\":\"jij@example.com\"}",
                        "Cookies wissen → GET cart is leeg (nieuwe sessie)"
                )
        );
    }

    @PostMapping("/cart")
    public Map<String, Object> addToCart(@RequestBody AddItemRequest body, HttpServletRequest request) {
        audit.bind(request);
        if (body.product() == null || body.product().isBlank() || body.qty() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product + qty>=1 verplicht");
        }
        if (!inventory.stock().containsKey(body.product())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Onbekend product");
        }
        cart.add(body.product(), body.qty());
        return cartView();
    }

    @GetMapping("/cart")
    public Map<String, Object> viewCart(HttpServletRequest request) {
        audit.bind(request);
        return cartView();
    }

    @DeleteMapping("/cart")
    public Map<String, Object> clearCart(HttpServletRequest request) {
        audit.bind(request);
        cart.clear();
        return cartView();
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody CheckoutRequest body, HttpServletRequest request) {
        audit.bind(request);
        if (cart.lines().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Winkelwagen is leeg");
        }
        // Prototype: elke checkout krijgt een verse draft (niet gedeeld met andere checkouts).
        CheckoutDraft draft = drafts.getObject();
        draft.setCustomerEmail(body.email());

        for (ShoppingCart.CartLine line : cart.lines()) {
            if (!inventory.tryReserve(line.product(), line.qty())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Onvoldoende voorraad voor " + line.product() + " (request " + audit.requestId() + ")"
                );
            }
        }
        draft.markPaid();
        List<ShoppingCart.CartLine> purchased = cart.lines();
        cart.clear();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Bestelling geplaatst");
        result.put("draft", Map.of(
                "draftId", draft.draftId(),
                "email", draft.customerEmail(),
                "steps", draft.steps()
        ));
        result.put("items", purchased.stream().map(l -> Map.of("product", l.product(), "qty", l.qty())).toList());
        result.put("remainingStock", inventory.stock());
        result.put("request", auditView());
        result.put("note", "Nog een checkout → nieuwe draftId (prototype). Voorraad is gedeeld (singleton).");
        return result;
    }

    private Map<String, Object> cartView() {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("items", cart.lines().stream()
                .map(l -> Map.of("product", l.product(), "qty", l.qty()))
                .toList());
        view.put("totalItems", cart.totalItems());
        view.put("request", auditView());
        view.put("scopeHint", "ShoppingCart is session-scoped — bewaar de JSESSIONID-cookie om items te houden.");
        return view;
    }

    private Map<String, Object> auditView() {
        return Map.of(
                "requestId", audit.requestId(),
                "path", audit.path(),
                "startedAt", audit.startedAt().toString()
        );
    }

    public record AddItemRequest(String product, int qty) {
    }

    public record CheckoutRequest(String email) {
    }
}
