package ch.rolfsblog.oop.procedural;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProceduralBookingTest {

    @BeforeEach
    void resetSharedState() {
        ProceduralBooking.reset();
    }

    @Test
    @DisplayName("Vergessenes freeSeats-- führt zur Überbuchung: 14 Leute für 12 Plätze")
    void forgottenDecrementLeadsToOverbooking() {
        for (int number = 1; number <= 14; number++) {
            ProceduralBooking.book("Participant " + number);
        }

        assertEquals(14, ProceduralBooking.bookedParticipants());
    }
}
