package ch.rolfsblog.oop.sealed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    private static void assertPrice(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }

    @Test
    @DisplayName("Record Pattern: Präsenzkurs")
    void pricesClassroomCourse() {
        assertPrice("2400", calculator.priceOf(
                new ClassroomCourse("Spring Boot Grundlagen", 3, new BigDecimal("800"))));
    }

    @Test
    @DisplayName("Record Pattern: Online-Kurs")
    void pricesOnlineCourse() {
        assertPrice("490", calculator.priceOf(
                new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490"))));
    }

    @Test
    @DisplayName("Record Pattern: Lehrgang, rekursiv über die Module")
    void pricesProgramRecursively() {
        Program program = new Program("Spring-Lehrgang", List.of(
                new ClassroomCourse("Spring Boot", 3, new BigDecimal("800")),
                new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490")),
                new ClassroomCourse("Spring Modulith", 2, new BigDecimal("800"))),
                new BigDecimal("500"));

        assertPrice("3990", calculator.priceOf(program));
    }

    @Test
    @DisplayName("Preis nie negativ, auch bei grossem Rabatt")
    void programPriceIsNeverNegative() {
        Program program = new Program("Schnupperpaket", List.of(
                new OnlineCourse("Java Basics", "Zoom", new BigDecimal("100"))),
                new BigDecimal("500"));

        assertPrice("0", calculator.priceOf(program));
    }

    @Test
    @DisplayName("Compact Constructor kopiert die Module")
    void programModulesAreUnmodifiable() {
        Program program = new Program("Spring-Lehrgang", List.of(
                new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490"))),
                BigDecimal.ZERO);

        assertThrows(UnsupportedOperationException.class,
                () -> program.modules().add(new OnlineCourse("Extra", "Zoom", BigDecimal.ONE)));
    }
}
