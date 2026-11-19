package ch.rolfsblog.oop.invoice;

import ch.rolfsblog.oop.course.ClassroomCourse;
import ch.rolfsblog.oop.course.CourseOffering;
import ch.rolfsblog.oop.course.OnlineCourse;
import ch.rolfsblog.oop.course.Program;

import java.math.BigDecimal;
import java.util.List;

/**
 * Counter-example from part 2: an invoice that checks the type of every
 * position and applies the pricing rules itself.
 * <p>
 * Bug on purpose: the branch for {@link Program} forgets the package discount,
 * so a participant of a program pays the full price.
 */
public final class TypeCheckingInvoice {

    private final List<CourseOffering> offerings;

    /**
     * Creates an invoice.
     *
     * @param offerings the booked course offerings
     */
    public TypeCheckingInvoice(List<CourseOffering> offerings) {
        this.offerings = List.copyOf(offerings);
    }

    /**
     * Returns the total of all positions.
     *
     * @return the sum of the prices, computed with type checks
     */
    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (CourseOffering offering : offerings) {
            total = total.add(priceOf(offering));
        }
        return total;
    }

    private static BigDecimal priceOf(CourseOffering offering) {
        if (offering instanceof ClassroomCourse course) {
            return course.dailyRate().multiply(BigDecimal.valueOf(course.days()));
        } else if (offering instanceof OnlineCourse course) {
            return course.flatPrice();
        } else if (offering instanceof Program program) {
            BigDecimal sum = BigDecimal.ZERO;
            for (CourseOffering module : program.modules()) {
                sum = sum.add(priceOf(module));
            }
            return sum; // bug: the package discount is forgotten
        }
        throw new IllegalArgumentException("Unknown course offering: " + offering.title());
    }
}
