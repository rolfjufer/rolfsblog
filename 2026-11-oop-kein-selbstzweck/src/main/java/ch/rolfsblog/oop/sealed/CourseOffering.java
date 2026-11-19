package ch.rolfsblog.oop.sealed;

/**
 * A course offering that can be booked.
 * Sealed: only the permitted types may implement it, so the compiler
 * can check a switch over all of them for completeness.
 */
public sealed interface CourseOffering permits ClassroomCourse, OnlineCourse, Program {
}
