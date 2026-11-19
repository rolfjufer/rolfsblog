package ch.rolfsblog.oop.identity;

import java.math.BigDecimal;

/**
 * A price as an immutable value object. The record generates
 * {@code equals()} and {@code hashCode()} over all components.
 * <p>
 * Pitfall: {@link BigDecimal#equals(Object)} takes the scale into account,
 * so a price of 10.0 is not equal to a price of 10.00.
 *
 * @param amount the amount
 * @param currency the ISO currency code, e.g. CHF
 */
public record Price(BigDecimal amount, String currency) {
}
