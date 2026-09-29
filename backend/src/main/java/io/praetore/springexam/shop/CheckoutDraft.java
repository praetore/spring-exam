package io.praetore.springexam.shop;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Scope: <b>prototype</b> — elke keer dat je de bean uit de context haalt, krijg je een <em>nieuwe</em> instance.
 * <p>
 * Waarom hier? Een checkout-draft is tijdelijke, veranderlijke staat (e-mail, stappen).
 * Die mag je niet delen tussen twee gelijktijdige checkouts: anders overschrijft klant A
 * de draft van klant B. Prototype voorkomt dat.
 * <p>
 * Exam-tip: injecteer prototype <b>niet</b> als gewoon veld in een singleton
 * (dan krijg je één bevroren instance). Gebruik {@code ObjectProvider} of {@code @Lookup}.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CheckoutDraft {

    private final String draftId = UUID.randomUUID().toString().substring(0, 8);
    private final List<String> steps = new ArrayList<>();
    private String customerEmail;

    public CheckoutDraft() {
        steps.add("created");
    }

    public String draftId() {
        return draftId;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        steps.add("email-set");
    }

    public void markPaid() {
        steps.add("paid");
    }

    public String customerEmail() {
        return customerEmail;
    }

    public List<String> steps() {
        return List.copyOf(steps);
    }
}
