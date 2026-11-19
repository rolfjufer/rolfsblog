package ch.rolfsblog.oop.payment;

import java.math.BigDecimal;

/**
 * A way to pay for an enrollment.
 * Callers only depend on this contract; how the payment is carried out
 * is up to the implementation.
 */
public interface PaymentMethod {

    /**
     * Charges the given amount.
     *
     * @param amount the amount to charge, must be positive
     * @return the result of the payment attempt
     */
    PaymentResult process(BigDecimal amount);
}
