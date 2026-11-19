package ch.rolfsblog.oop.sealed;

import java.math.BigDecimal;
import java.util.List;

/**
 * A program that bundles several course offerings at a package discount.
 *
 * @param title the title of the program
 * @param modules the course offerings included in the program
 * @param discount the discount on the sum of the module prices
 */
public record Program(String title, List<CourseOffering> modules, BigDecimal discount)
        implements CourseOffering {

    /**
     * Creates a program and stores an unmodifiable copy of the given modules.
     */
    public Program {
        modules = List.copyOf(modules);
    }
}
