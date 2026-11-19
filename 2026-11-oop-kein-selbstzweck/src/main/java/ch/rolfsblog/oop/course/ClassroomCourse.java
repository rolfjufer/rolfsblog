package ch.rolfsblog.oop.course;

import java.math.BigDecimal;

/**
 * A classroom course priced per day.
 */
public class ClassroomCourse extends CourseOffering {

    private final int days;
    private final BigDecimal dailyRate;

    /**
     * Creates a classroom course.
     *
     * @param title the title of the course
     * @param days the number of course days, must be positive
     * @param dailyRate the price per day
     * @throws IllegalArgumentException if days is not positive
     */
    public ClassroomCourse(String title, int days, BigDecimal dailyRate) {
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive");
        }
        super(title); // statements before super() allowed since Java 25 (JEP 513)
        this.days = days;
        this.dailyRate = dailyRate;
    }

    /**
     * Returns the number of course days.
     *
     * @return the number of course days
     */
    public int days() {
        return days;
    }

    /**
     * Returns the price per day.
     *
     * @return the daily rate
     */
    public BigDecimal dailyRate() {
        return dailyRate;
    }

    @Override
    public BigDecimal price() {
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }
}
