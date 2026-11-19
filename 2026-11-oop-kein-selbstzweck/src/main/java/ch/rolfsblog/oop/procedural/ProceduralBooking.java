package ch.rolfsblog.oop.procedural;

import java.util.ArrayList;
import java.util.List;

/**
 * Starting point of part 1: booking data kept in loose static variables.
 * <p>
 * Every method can read and change every variable. The class deliberately
 * contains the bug from the blog post: {@link #book(String)} forgets to
 * decrement {@link #freeSeats}, so the course gets overbooked.
 */
public final class ProceduralBooking {

    static String course = "Spring Boot Grundlagen";
    static int freeSeats = 12;
    static boolean paid = false;
    static final List<String> participants = new ArrayList<>();

    private ProceduralBooking() {
    }

    /**
     * Books a participant if seats are left.
     * Bug on purpose: the line {@code freeSeats--} is missing.
     *
     * @param participant the name of the participant
     */
    public static void book(String participant) {
        if (freeSeats > 0) {
            participants.add(participant);
            // freeSeats--;   <- forgotten, see blog post part 1
        }
    }

    /**
     * Returns the number of booked participants.
     *
     * @return the number of booked participants
     */
    public static int bookedParticipants() {
        return participants.size();
    }

    /**
     * Resets the shared static state. Needed because every caller,
     * including every test, works on the same variables.
     */
    public static void reset() {
        course = "Spring Boot Grundlagen";
        freeSeats = 12;
        paid = false;
        participants.clear();
    }
}
