package ch.rolfsblog.oop.sealed;

import java.math.BigDecimal;

/**
 * A classroom course priced per day.
 *
 * @param title the title of the course
 * @param days the number of course days
 * @param dailyRate the price per day
 */
public record ClassroomCourse(String title, int days, BigDecimal dailyRate)
        implements CourseOffering {
}
