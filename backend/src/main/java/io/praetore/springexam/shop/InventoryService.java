package io.praetore.springexam.shop;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scope: <b>singleton</b> (default) — één gedeelde instance voor de hele ApplicationContext.
 * <p>
 * Waarom hier? Voorraad is app-breed: als request A 2 mokken reserveert, moet request B
 * dat meteen zien. Singleton is de normale keuze voor services zonder gebruikers-specifieke staat.
 * <p>
 * Exam-tip: singleton is thread-shared — mutable state (zoals deze map) moet je veilig bijwerken
 * (hier: {@code synchronized} op reserveren).
 */
@Component
public class InventoryService {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
            "spring-mug", 10,
            "exam-guide", 5,
            "sticker-pack", 50
    ));

    public Map<String, Integer> stock() {
        return Map.copyOf(stock);
    }

    public synchronized boolean tryReserve(String product, int qty) {
        int available = stock.getOrDefault(product, 0);
        if (available < qty) {
            return false;
        }
        stock.put(product, available - qty);
        return true;
    }
}
