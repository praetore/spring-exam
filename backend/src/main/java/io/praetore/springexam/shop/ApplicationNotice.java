package io.praetore.springexam.shop;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Scope: <b>application</b> — Spring bewaart de target-instance op de {@code ServletContext}.
 * <p>
 * Daardoor kan ook niet-Spring code (oude filter, JSP {@code applicationScope}, …) de waarde
 * lezen zonder de Spring container te kennen. Dat is hét praktische verschil met singleton.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationNotice {

    private String text = "application-notice";

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
