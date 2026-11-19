package ch.rolfsblog.oop.course;

import java.math.BigDecimal;
import java.util.List;

/**
 * A program ("Lehrgang") that bundles several course offerings at a package discount.
 * <p>
 * Inheritance and composition at the same time: a program <em>is</em> a course
 * offering and <em>has</em> course offerings (composite pattern).
 */
public class Program extends CourseOffering {

    private final List<CourseOffering> modules;
    private final BigDecimal discount;

    /**
     * Creates a program.
     *
     * @param title the title of the program
     * @param modules the course offerings included in the program
     * @param discount the discount on the sum of the module prices
     */
    public Program(String title, List<CourseOffering> modules, BigDecimal discount) {
        super(title);
        this.modules = List.copyOf(modules);
        this.discount = discount;
    }

    /**
     * Returns the included course offerings.
     *
     * @return an unmodifiable list of the modules
     */
    public List<CourseOffering> modules() {
        return modules;
    }

    /**
     * Returns the package discount.
     *
     * @return the discount on the sum of the module prices
     */
    public BigDecimal discount() {
        return discount;
    }

    /**
     * Returns the sum of the module prices minus the discount.
     * Never negative, to keep the contract of {@link CourseOffering#price()}.
     *
     * @return the price of the program, never negative
     */
    @Override
    public BigDecimal price() {
        BigDecimal sum = modules.stream()
                .map(CourseOffering::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.subtract(discount).max(BigDecimal.ZERO);
    }
}
