package ch.rolfsblog.oop.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseOfferingTest {

    private static void assertPrice(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }

    @Test
    @DisplayName("Präsenzkurs: Tagessatz mal Tage")
    void classroomCoursePricePerDay() {
        CourseOffering course = new ClassroomCourse("Spring Boot Grundlagen", 3, new BigDecimal("800"));

        assertPrice("2400", course.price());
    }

    @Test
    @DisplayName("Präsenzkurs prüft seine Invariante vor super() (JEP 513)")
    void classroomCourseRejectsNonPositiveDays() {
        assertThrows(IllegalArgumentException.class,
                () -> new ClassroomCourse("Spring Boot Grundlagen", 0, new BigDecimal("800")));
    }

    @Test
    @DisplayName("Online-Kurs: Pauschalpreis")
    void onlineCourseFlatPrice() {
        CourseOffering course = new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490"));

        assertPrice("490", course.price());
    }

    @Test
    @DisplayName("Lehrgang: Summe der Module minus Paketrabatt")
    void programPriceWithDiscount() {
        CourseOffering program = new Program("Spring-Lehrgang", List.of(
                new ClassroomCourse("Spring Boot", 3, new BigDecimal("800")),
                new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490")),
                new ClassroomCourse("Spring Modulith", 2, new BigDecimal("800"))),
                new BigDecimal("500"));

        assertPrice("3990", program.price());
    }

    @Test
    @DisplayName("Lehrgang hält den Vertrag ein: Preis nie negativ (LSP)")
    void programPriceIsNeverNegative() {
        CourseOffering program = new Program("Schnupperpaket", List.of(
                new OnlineCourse("Java Basics", "Zoom", new BigDecimal("100"))),
                new BigDecimal("500"));

        assertPrice("0", program.price());
    }

    @Test
    @DisplayName("Lehrgang kopiert seine Module (Kapselung)")
    void programModulesAreUnmodifiable() {
        Program program = new Program("Spring-Lehrgang", List.of(
                new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490"))),
                BigDecimal.ZERO);

        assertThrows(UnsupportedOperationException.class,
                () -> program.modules().add(new OnlineCourse("Extra", "Zoom", BigDecimal.ONE)));
    }
}
