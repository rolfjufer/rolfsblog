package ch.rolfsblog.oop.identity;

import ch.rolfsblog.oop.course.Participant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentityTest {

    @Test
    @DisplayName("Entity: gleiche ID heisst gleiche Teilnehmerin, auch bei anderem Namen")
    void participantsWithSameIdAreEqual() {
        Participant before = new Participant(42, "Anna Meier");
        Participant after = new Participant(42, "Anna Müller");

        assertNotSame(before, after);
        assertEquals(before, after);
        assertEquals(before.hashCode(), after.hashCode());
    }

    @Test
    @DisplayName("Entity: Umbenennen ändert die Identität nicht")
    void renamingKeepsIdentityInHashSet() {
        Participant anna = new Participant(42, "Anna Meier");
        Set<Participant> participants = new HashSet<>();
        participants.add(anna);

        anna.rename("Anna Müller");

        assertTrue(participants.contains(new Participant(42, "Anna Müller")));
    }

    @Test
    @DisplayName("Record: vergleicht alle Komponenten")
    void recordsCompareAllComponents() {
        Price chf = new Price(new BigDecimal("10.00"), "CHF");

        assertEquals(new Price(new BigDecimal("10.00"), "CHF"), chf);
        assertNotEquals(new Price(new BigDecimal("10.00"), "EUR"), chf);
    }

    @Test
    @DisplayName("Fallstrick: BigDecimal.equals() berücksichtigt die Skala")
    void bigDecimalScaleBreaksRecordEquality() {
        Price tenPointZero = new Price(new BigDecimal("10.0"), "CHF");
        Price tenPointZeroZero = new Price(new BigDecimal("10.00"), "CHF");

        assertNotEquals(tenPointZero, tenPointZeroZero);
        assertEquals(0, tenPointZero.amount().compareTo(tenPointZeroZero.amount()));
    }
}
