package ch.rolfsblog.oop.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentMethodTest {

    @Test
    @DisplayName("CardPayment ist nur eine Skizze")
    void cardPaymentIsASketch() {
        PaymentMethod card = new CardPayment();

        assertThrows(UnsupportedOperationException.class, () -> card.process(BigDecimal.TEN));
    }

    @Test
    @DisplayName("TwintPayment ist nur eine Skizze")
    void twintPaymentIsASketch() {
        PaymentMethod twint = new TwintPayment();

        assertThrows(UnsupportedOperationException.class, () -> twint.process(BigDecimal.TEN));
    }
}
