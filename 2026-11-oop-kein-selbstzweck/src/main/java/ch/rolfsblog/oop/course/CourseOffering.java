package ch.rolfsblog.oop.course;

import java.math.BigDecimal;

/**
 * A course offering that can be booked: the common base of all offering types.
 */
public abstract class CourseOffering {

    private final String title;

    /**
     * Creates a course offering.
     *
     * @param title the title of the offering
     */
    protected CourseOffering(String title) {
        this.title = title;
    }

    /**
     * Returns the title.
     *
     * @return the title of the offering
     */
    public String title() {
        return title;
    }

    /**
     * Returns the price. Every subclass computes it according to its own rules.
     *
     * @return the price of this offering, never negative
     */
    public abstract BigDecimal price();
}
