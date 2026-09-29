package io.praetore.springexam.shop;

import org.springframework.stereotype.Component;

/**
 * Scope: <b>singleton</b> — leeft in de Spring {@code ApplicationContext}, niet automatisch
 * als attribuut op de {@code ServletContext}.
 * <p>
 * Vergelijk met {@link ApplicationNotice}: die is wél terug te vinden via
 * {@code servletContext.getAttribute(...)}.
 */
@Component
public class SingletonNotice {

    private String text = "singleton-notice";

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
