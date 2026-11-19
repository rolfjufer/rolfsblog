package ch.rolfsblog.oop.course;

import ch.rolfsblog.oop.payment.PaymentMethod;
import ch.rolfsblog.oop.payment.PaymentResult;

/**
 * The enrollment of a participant in a course offering.
 * Responsible for its own payment status.
 */
public class Enrollment {

    private final Participant participant;
    private final CourseOffering offering;
    private PaymentStatus paymentStatus;

    /**
     * Creates an open, not yet paid enrollment.
     *
     * @param participant the enrolled participant
     * @param offering the booked course offering
     */
    public Enrollment(Participant participant, CourseOffering offering) {
        this.participant = participant;
        this.offering = offering;
        this.paymentStatus = PaymentStatus.OPEN;
    }

    /**
     * Returns the enrolled participant.
     *
     * @return the participant
     */
    public Participant participant() {
        return participant;
    }

    /**
     * Returns the booked course offering.
     *
     * @return the course offering
     */
    public CourseOffering offering() {
        return offering;
    }

    /**
     * Returns the current payment status.
     *
     * @return the payment status
     */
    public PaymentStatus paymentStatus() {
        return paymentStatus;
    }

    /**
     * Marks this enrollment as paid. Requires proof of a successful payment.
     *
     * @param result the result of the payment attempt
     * @throws IllegalArgumentException if the payment was not successful
     * @throws IllegalStateException if the enrollment is already paid
     */
    public void markAsPaid(PaymentResult result) {
        if (!result.successful()) {
            throw new IllegalArgumentException("Payment not confirmed");
        }
        if (paymentStatus == PaymentStatus.PAID) {
            throw new IllegalStateException("Enrollment is already paid");
        }
        this.paymentStatus = PaymentStatus.PAID;
    }

    /**
     * Pays the price of the booked offering with the given payment method.
     * The enrollment only knows the abstraction {@link PaymentMethod},
     * not how the payment is carried out.
     *
     * @param paymentMethod the payment method to use
     * @return the result of the payment attempt
     */
    public PaymentResult pay(PaymentMethod paymentMethod) {
        PaymentResult result = paymentMethod.process(offering.price());
        if (result.successful()) {
            markAsPaid(result);
        }
        return result;
    }
}
