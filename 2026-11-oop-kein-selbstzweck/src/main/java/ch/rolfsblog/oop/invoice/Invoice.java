package ch.rolfsblog.oop.invoice;

import ch.rolfsblog.oop.course.CourseOffering;

import java.math.BigDecimal;
import java.util.List;

/**
 * An invoice that asks every position for its price (polymorphism).
 * It does not know a single pricing rule.
 */
public final class Invoice {

    private final List<CourseOffering> offerings;

    /**
     * Creates an invoice.
     *
     * @param offerings the booked course offerings
     */
    public Invoice(List<CourseOffering> offerings) {
        this.offerings = List.copyOf(offerings);
    }

    /**
     * Returns the booked course offerings.
     *
     * @return an unmodifiable list of the offerings
     */
    public List<CourseOffering> offerings() {
        return offerings;
    }

    /**
     * Returns the total of all positions.
     *
     * @return the sum of the prices of all offerings
     */
    public BigDecimal total() {
        return offerings.stream()
                .map(CourseOffering::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
