package io.praetore.springexam.shop;

import jakarta.servlet.ServletContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Toont het verschil singleton vs application:
 * <ul>
 *   <li>Via Spring zie je beide</li>
 *   <li>Via {@link ServletContext} zie je (vrijwel) alleen de application-scoped bean</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/shop/scope-diff")
public class ScopeDiffController {

    private final SingletonNotice singletonNotice;
    private final ApplicationNotice applicationNotice;

    public ScopeDiffController(SingletonNotice singletonNotice, ApplicationNotice applicationNotice) {
        this.singletonNotice = singletonNotice;
        this.applicationNotice = applicationNotice;
    }

    @GetMapping
    public Map<String, Object> compare(ServletContext servletContext) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("viaSpring", Map.of(
                "singletonNotice", singletonNotice.text(),
                "applicationNotice", applicationNotice.text()
        ));
        body.put("viaServletContext", Map.of(
                "scopedTarget.applicationNotice", attr(servletContext, ServletContextOnlyFilterConfig.APPLICATION_ATTR),
                "singletonNotice", attr(servletContext, ServletContextOnlyFilterConfig.SINGLETON_ATTR)
        ));
        body.put("uitleg", Map.of(
                "singleton", "Wel via Spring injectie, niet als ServletContext-attribuut.",
                "application", "Wel via Spring én als ServletContext-attribuut (scopedTarget.<beanNaam>).",
                "volgendeStap", "POST /api/shop/scope-diff/notices om teksten te wijzigen, daarna opnieuw GET + /from-servlet-context"
        ));
        return body;
    }

    @PostMapping("/notices")
    public Map<String, Object> update(@RequestBody NoticesUpdate update) {
        if (update.singletonText() != null) {
            singletonNotice.setText(update.singletonText());
        }
        if (update.applicationText() != null) {
            applicationNotice.setText(update.applicationText());
        }
        return Map.of(
                "singletonNotice", singletonNotice.text(),
                "applicationNotice", applicationNotice.text()
        );
    }

    private static Object attr(ServletContext ctx, String name) {
        Object value = ctx.getAttribute(name);
        if (value == null) {
            return null;
        }
        try {
            return value.getClass().getMethod("text").invoke(value);
        } catch (ReflectiveOperationException ex) {
            return String.valueOf(value);
        }
    }

    public record NoticesUpdate(String singletonText, String applicationText) {
    }
}
