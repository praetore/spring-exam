package io.praetore.springexam.shop;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Scope: <b>session</b> — één winkelwagen per HTTP-sessie (cookie {@code JSESSIONID}).
 * <p>
 * Waarom hier? De klant doet meerdere calls (items toevoegen, cart bekijken, afrekenen).
 * Die staat hoort bij <em>deze browser-sessie</em>, niet bij één request en niet bij de hele app.
 * <p>
 * {@code proxyMode = TARGET_CLASS} is nodig omdat deze bean in de singleton-{@code ShopController}
 * wordt geïnjecteerd: Spring injecteert een proxy; pas bij gebruik wordt de echte session-bean gezocht.
 * Zonder proxy zou de container proberen de session-bean al bij het starten van de controller te maken.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {

    private final List<CartLine> lines = new ArrayList<>();

    public void add(String product, int qty) {
        lines.stream()
                .filter(line -> line.product().equals(product))
                .findFirst()
                .ifPresentOrElse(
                        line -> line.add(qty),
                        () -> lines.add(new CartLine(product, qty))
                );
    }

    public List<CartLine> lines() {
        return List.copyOf(lines);
    }

    public void clear() {
        lines.clear();
    }

    public int totalItems() {
        return lines.stream().mapToInt(CartLine::qty).sum();
    }

    public static final class CartLine {
        private final String product;
        private int qty;

        CartLine(String product, int qty) {
            this.product = product;
            this.qty = qty;
        }

        void add(int extra) {
            this.qty += extra;
        }

        public String product() {
            return product;
        }

        public int qty() {
            return qty;
        }
    }
}
