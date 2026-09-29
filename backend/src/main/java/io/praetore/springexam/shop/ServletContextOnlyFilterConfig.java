package io.praetore.springexam.shop;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Bewust een "domme" Servlet-{@link Filter} (geen Spring DI in de filter zelf).
 * Hij kent alleen de {@link ServletContext} — precies het scenario waarin application-scope telt.
 */
@Configuration
public class ServletContextOnlyFilterConfig {

    /** Spring legt application-scoped targets onder dit prefix + bean-naam. */
    static final String APPLICATION_ATTR = "scopedTarget.applicationNotice";
    static final String SINGLETON_ATTR = "singletonNotice";

    @Bean
    FilterRegistrationBean<Filter> servletContextOnlyFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ServletContextOnlyFilter());
        registration.addUrlPatterns("/api/shop/scope-diff/from-servlet-context");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    static final class ServletContextOnlyFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            ServletContext ctx = request.getServletContext();
            Object applicationTarget = ctx.getAttribute(APPLICATION_ATTR);
            Object singletonAttr = ctx.getAttribute(SINGLETON_ATTR);

            String applicationText = readText(applicationTarget);
            // Singleton staat NIET op de ServletContext → blijft null / ontbreekt.
            String singletonText = singletonAttr == null ? null : String.valueOf(singletonAttr);

            HttpServletResponse http = (HttpServletResponse) response;
            http.setStatus(200);
            http.setContentType("application/json;charset=UTF-8");
            http.getWriter().write("""
                    {
                      "gelezenDoor": "ruwe Servlet Filter (geen Spring @Autowired)",
                      "servletContext.applicationNotice": %s,
                      "servletContext.singletonNotice": %s,
                      "conclusie": "Alleen application-scope ligt op ServletContext; singleton alleen via Spring."
                    }
                    """.formatted(jsonString(applicationText), jsonString(singletonText)));
            // Niet door naar de controller — dit ís het antwoord.
        }

        private static String readText(Object bean) {
            if (bean == null) {
                return null;
            }
            try {
                Method text = bean.getClass().getMethod("text");
                Object value = text.invoke(bean);
                return value == null ? null : value.toString();
            } catch (ReflectiveOperationException ex) {
                return bean.toString();
            }
        }

        private static String jsonString(String value) {
            if (value == null) {
                return "null";
            }
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    }
}
