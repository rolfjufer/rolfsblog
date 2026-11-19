package ch.rolfsblog.oop.course;

import java.util.Objects;

/**
 * A participant of a course. An entity: two participants are equal
 * if they have the same id, even if their names differ.
 */
public final class Participant {

    private final long id;
    private String name;

    /**
     * Creates a participant.
     *
     * @param id the unique id of the participant
     * @param name the full name of the participant
     */
    public Participant(long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Returns the unique id.
     *
     * @return the id of the participant
     */
    public long id() {
        return id;
    }

    /**
     * Returns the current name.
     *
     * @return the full name of the participant
     */
    public String name() {
        return name;
    }

    /**
     * Changes the name. The identity of the participant stays the same.
     *
     * @param newName the new full name
     */
    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName, "newName");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Participant participant)) {
            return false;
        }
        return id == participant.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Participant[id=" + id + ", name=" + name + "]";
    }
}
