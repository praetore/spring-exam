package io.praetore.springexam.shop;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scope: <b>application</b> — één instance per ServletContext (de webapp).
 * <p>
 * Waarom hier? Een simpele teller van storefront-hits die voor alle sessies/requests telt.
 * In een typische Boot-app lijkt dit sterk op singleton; het verschil is dat de lifecycle
 * aan de web {@code ServletContext} hangt (relevant bij oudere war-deployments / gedeelde context).
 * <p>
 * Exam: ken het bestaan; in de praktijk kies je meestal singleton tenzij je expliciet
 * ServletContext-scope nodig hebt.
 * <p>
 * Concrete vergelijking singleton vs application: {@code /api/shop/scope-diff}
 * en {@code backend/http/scope-diff.http}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StorefrontStats {

    private final AtomicInteger pageHits = new AtomicInteger();

    public int hit() {
        return pageHits.incrementAndGet();
    }

    public int pageHits() {
        return pageHits.get();
    }
}
