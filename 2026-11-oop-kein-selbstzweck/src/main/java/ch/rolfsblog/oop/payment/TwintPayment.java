package ch.rolfsblog.oop.payment;

import java.math.BigDecimal;

/**
 * Pays via TWINT.
 */
public class TwintPayment implements PaymentMethod {

    @Override
    public PaymentResult process(BigDecimal amount) {
        // sketch: calls the TWINT provider
        throw new UnsupportedOperationException("not implemented");
    }
}
