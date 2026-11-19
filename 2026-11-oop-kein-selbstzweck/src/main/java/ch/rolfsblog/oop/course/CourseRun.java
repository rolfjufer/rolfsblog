package ch.rolfsblog.oop.course;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a concrete run of a course with a limited number of participants.
 * Keeps track of all enrollments for this course run.
 */
public class CourseRun {

    private final CourseOffering offering;
    private final int maxParticipants;
    private final List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Creates a new course run for the given course offering.
     *
     * @param offering the course offering for this course run
     * @param maxParticipants the maximum number of participants allowed
     */
    public CourseRun(CourseOffering offering, int maxParticipants) {
        this.offering = offering;
        this.maxParticipants = maxParticipants;
    }

    /**
     * Enrolls a participant in this course run.
     *
     * @param participant the participant to enroll
     * @return the newly created enrollment
     * @throws IllegalStateException if the course run is fully booked
     */
    public Enrollment enroll(Participant participant) {
        if (enrollments.size() >= maxParticipants) {
            throw new IllegalStateException("Course run is fully booked");
        }
        Enrollment enrollment = new Enrollment(participant, offering);
        enrollments.add(enrollment);
        return enrollment;
    }

    /**
     * Returns the number of free seats. Derived from the list of enrollments,
     * so there is no separate counter that could get out of sync.
     *
     * @return the number of free seats
     */
    public int freeSeats() {
        return maxParticipants - enrollments.size();
    }

    /**
     * Returns an unmodifiable snapshot of all enrollments in this course run.
     *
     * @return a copy of the current enrollments
     */
    public List<Enrollment> enrollments() {
        return List.copyOf(enrollments);
    }
}
