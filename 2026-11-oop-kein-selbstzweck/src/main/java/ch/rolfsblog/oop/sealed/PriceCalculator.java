package ch.rolfsblog.oop.sealed;

import java.math.BigDecimal;
import java.util.List;

/**
 * Calculates the price of any course offering.
 */
public final class PriceCalculator {

    /**
     * Returns the price of the given course offering.
     *
     * @param offering the course offering to price
     * @return the price of the offering, never negative
     */
    public BigDecimal priceOf(CourseOffering offering) {
        return switch (offering) {
            case ClassroomCourse(_, int days, BigDecimal dailyRate) ->
                    dailyRate.multiply(BigDecimal.valueOf(days));
            case OnlineCourse(_, _, BigDecimal flatPrice) -> flatPrice;
            case Program(_, List<CourseOffering> modules, BigDecimal discount) -> modules.stream()
                    .map(this::priceOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .subtract(discount)
                    .max(BigDecimal.ZERO);
        };
    }
}
