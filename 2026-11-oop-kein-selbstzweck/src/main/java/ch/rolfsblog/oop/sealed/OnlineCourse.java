package ch.rolfsblog.oop.sealed;

import java.math.BigDecimal;

/**
 * An online course with a flat price.
 *
 * @param title the title of the course
 * @param platform the platform the course runs on
 * @param flatPrice the flat price of the course
 */
public record OnlineCourse(String title, String platform, BigDecimal flatPrice)
        implements CourseOffering {
}
