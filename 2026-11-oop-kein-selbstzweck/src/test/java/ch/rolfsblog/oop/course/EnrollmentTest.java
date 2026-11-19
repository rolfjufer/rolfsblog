package ch.rolfsblog.oop.course;

import ch.rolfsblog.oop.payment.PaymentMethod;
import ch.rolfsblog.oop.payment.PaymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnrollmentTest {

    private final CourseOffering springBootBasics =
            new ClassroomCourse("Spring Boot Grundlagen", 3, new BigDecimal("800"));
    private final Participant anna = new Participant(1, "Anna Meier");

    @Test
    @DisplayName("Eine neue Anmeldung ist offen")
    void newEnrollmentIsOpen() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);

        assertEquals(PaymentStatus.OPEN, enrollment.paymentStatus());
    }

    @Test
    @DisplayName("Mit bestätigter Zahlung wird die Anmeldung bezahlt")
    void marksAsPaidWithConfirmedPayment() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);

        enrollment.markAsPaid(new PaymentResult(true, "TX-1"));

        assertEquals(PaymentStatus.PAID, enrollment.paymentStatus());
    }

    @Test
    @DisplayName("Ohne bestätigte Zahlung bleibt die Anmeldung offen (falsches Argument)")
    void rejectsUnconfirmedPayment() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);

        assertThrows(IllegalArgumentException.class,
                () -> enrollment.markAsPaid(new PaymentResult(false, "TX-2")));
        assertEquals(PaymentStatus.OPEN, enrollment.paymentStatus());
    }

    @Test
    @DisplayName("Eine bezahlte Anmeldung kann nicht nochmals bezahlt werden (falscher Zustand)")
    void rejectsSecondPayment() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);
        enrollment.markAsPaid(new PaymentResult(true, "TX-3"));

        assertThrows(IllegalStateException.class,
                () -> enrollment.markAsPaid(new PaymentResult(true, "TX-4")));
    }

    @Test
    @DisplayName("pay() fordert den Preis über die Abstraktion PaymentMethod ein")
    void paysThePriceThroughThePaymentMethod() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);
        AtomicReference<BigDecimal> chargedAmount = new AtomicReference<>();
        PaymentMethod fakePayment = amount -> {
            chargedAmount.set(amount);
            return new PaymentResult(true, "TX-5");
        };

        PaymentResult result = enrollment.pay(fakePayment);

        assertTrue(result.successful());
        assertEquals(0, new BigDecimal("2400").compareTo(chargedAmount.get()));
        assertEquals(PaymentStatus.PAID, enrollment.paymentStatus());
    }

    @Test
    @DisplayName("Eine fehlgeschlagene Zahlung lässt die Anmeldung offen")
    void failedPaymentLeavesEnrollmentOpen() {
        Enrollment enrollment = new Enrollment(anna, springBootBasics);
        PaymentMethod decliningPayment = amount -> new PaymentResult(false, "TX-6");

        PaymentResult result = enrollment.pay(decliningPayment);

        assertFalse(result.successful());
        assertEquals(PaymentStatus.OPEN, enrollment.paymentStatus());
    }
}
