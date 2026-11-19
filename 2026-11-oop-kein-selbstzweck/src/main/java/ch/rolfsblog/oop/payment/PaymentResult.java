package ch.rolfsblog.oop.payment;

/**
 * Result of a payment attempt, as reported by the payment provider.
 *
 * @param successful whether the payment was successful
 * @param transactionId the transaction id of the provider
 */
public record PaymentResult(boolean successful, String transactionId) {
}
