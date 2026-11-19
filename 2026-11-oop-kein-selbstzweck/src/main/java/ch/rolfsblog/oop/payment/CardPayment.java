package ch.rolfsblog.oop.payment;

import java.math.BigDecimal;

/**
 * Pays by credit card via the card payment gateway.
 */
public class CardPayment implements PaymentMethod {

    @Override
    public PaymentResult process(BigDecimal amount) {
        // sketch: calls the card payment gateway
        throw new UnsupportedOperationException("not implemented");
    }
}
