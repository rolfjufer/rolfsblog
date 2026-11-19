package ch.rolfsblog.oop.invoice;

import ch.rolfsblog.oop.course.ClassroomCourse;
import ch.rolfsblog.oop.course.CourseOffering;
import ch.rolfsblog.oop.course.OnlineCourse;
import ch.rolfsblog.oop.course.Program;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceTest {

    private final List<CourseOffering> positions = List.of(
            new ClassroomCourse("Spring Boot Grundlagen", 3, new BigDecimal("800")),
            new OnlineCourse("Spring Data", "Zoom", new BigDecimal("490")),
            new Program("Spring-Lehrgang", List.of(
                    new ClassroomCourse("Spring Boot", 3, new BigDecimal("800")),
                    new ClassroomCourse("Spring Modulith", 2, new BigDecimal("800"))),
                    new BigDecimal("500")));

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }

    @Test
    @DisplayName("Polymorphie: die Rechnung fragt jede Position nach ihrem Preis")
    void polymorphicInvoiceAppliesEveryRule() {
        // 2400 + 490 + (2400 + 1600 - 500)
        assertAmount("6390", new Invoice(positions).total());
    }

    @Test
    @DisplayName("Typprüfung: die if-Kaskade vergisst den Paketrabatt (Fehler vom Montag)")
    void typeCheckingInvoiceForgetsTheDiscount() {
        // 2400 + 490 + (2400 + 1600), discount of 500 missing
        assertAmount("6890", new TypeCheckingInvoice(positions).total());
    }
}
