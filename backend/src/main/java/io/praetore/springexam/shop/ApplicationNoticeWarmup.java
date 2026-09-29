package io.praetore.springexam.shop;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Zorgt dat de application-scoped bean meteen op de {@code ServletContext} belandt,
 * zodat de ruwe Servlet-filter hem ook kan vinden zonder eerst een controller-call.
 */
@Component
public class ApplicationNoticeWarmup implements ApplicationRunner {

    private final ApplicationNotice applicationNotice;

    public ApplicationNoticeWarmup(ApplicationNotice applicationNotice) {
        this.applicationNotice = applicationNotice;
    }

    @Override
    public void run(ApplicationArguments args) {
        applicationNotice.text();
    }
}
