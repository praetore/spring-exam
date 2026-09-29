package io.praetore.springexam.shop;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.UUID;

/**
 * Scope: <b>request</b> — één instance per HTTP-request, daarna weg.
 * <p>
 * Waarom hier? Een correlation-/request-id voor logging en foutmeldingen hoort bij
 * <em>deze ene call</em>. De volgende request krijgt een nieuw id — handig om logs te volgen.
 * <p>
 * Ook hier scoped proxy: de bean leeft korter dan de singleton-controller waarin hij zit.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestAudit {

    private final String requestId = UUID.randomUUID().toString().substring(0, 8);
    private final Instant startedAt = Instant.now();
    private String path;
    private String client;

    public void bind(HttpServletRequest request) {
        this.path = request.getRequestURI();
        this.client = request.getRemoteAddr();
    }

    public String requestId() {
        return requestId;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public String path() {
        return path;
    }

    public String client() {
        return client;
    }
}
