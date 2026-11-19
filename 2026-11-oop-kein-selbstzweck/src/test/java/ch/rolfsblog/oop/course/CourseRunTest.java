package ch.rolfsblog.oop.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseRunTest {

    private final CourseOffering springBootBasics =
            new ClassroomCourse("Spring Boot Grundlagen", 3, new BigDecimal("800"));

    @Test
    @DisplayName("Die dreizehnte Anmeldung wird abgewiesen")
    void rejectsEnrollmentWhenFullyBooked() {
        CourseRun run = new CourseRun(springBootBasics, 12);
        for (int number = 1; number <= 12; number++) {
            run.enroll(new Participant(number, "Participant " + number));
        }

        assertThrows(IllegalStateException.class,
                () -> run.enroll(new Participant(13, "Participant 13")));
        assertEquals(12, run.enrollments().size());
        assertEquals(0, run.freeSeats());
    }

    @Test
    @DisplayName("Freie Plätze werden aus der Liste abgeleitet, nicht separat gezählt")
    void freeSeatsAreDerivedFromEnrollments() {
        CourseRun run = new CourseRun(springBootBasics, 12);
        run.enroll(new Participant(1, "Anna Meier"));
        run.enroll(new Participant(2, "Marco Rossi"));

        assertEquals(10, run.freeSeats());
    }

    @Test
    @DisplayName("enrollments() liefert eine unveränderliche Kopie")
    void enrollmentsAreAnUnmodifiableSnapshot() {
        CourseRun run = new CourseRun(springBootBasics, 12);
        run.enroll(new Participant(1, "Anna Meier"));
        List<Enrollment> snapshot = run.enrollments();
        Enrollment intruder = new Enrollment(new Participant(99, "Intruder"), springBootBasics);

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(intruder));

        run.enroll(new Participant(2, "Marco Rossi"));
        assertEquals(1, snapshot.size());
        assertEquals(2, run.enrollments().size());
    }
}
