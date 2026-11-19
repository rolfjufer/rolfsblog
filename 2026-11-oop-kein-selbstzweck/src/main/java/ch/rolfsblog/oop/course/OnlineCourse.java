package ch.rolfsblog.oop.course;

import java.math.BigDecimal;

/**
 * An online course with a flat price.
 */
public class OnlineCourse extends CourseOffering {

    private final String platform;
    private final BigDecimal flatPrice;

    /**
     * Creates an online course.
     *
     * @param title the title of the course
     * @param platform the platform the course runs on
     * @param flatPrice the flat price of the course
     */
    public OnlineCourse(String title, String platform, BigDecimal flatPrice) {
        super(title);
        this.platform = platform;
        this.flatPrice = flatPrice;
    }

    /**
     * Returns the platform.
     *
     * @return the platform the course runs on
     */
    public String platform() {
        return platform;
    }

    /**
     * Returns the flat price.
     *
     * @return the flat price of the course
     */
    public BigDecimal flatPrice() {
        return flatPrice;
    }

    @Override
    public BigDecimal price() {
        return flatPrice;
    }
}
