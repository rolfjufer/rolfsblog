package ch.rolfsblog.oop.lsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LiskovTest {

    /**
     * Client code that relies on the contract of {@link Rectangle}.
     */
    private static int stretch(Rectangle rectangle) {
        rectangle.setWidth(5);
        rectangle.setHeight(4);
        return rectangle.area();
    }

    @Test
    @DisplayName("Rechteck erfüllt den Vertrag: Fläche 5 × 4 = 20")
    void rectangleKeepsItsContract() {
        assertEquals(20, stretch(new Rectangle(1, 1)));
    }

    @Test
    @DisplayName("Quadrat verletzt das LSP: Fläche 16 statt 20")
    void squareBreaksTheRectangleContract() {
        assertEquals(16, stretch(new Square(1)));
    }
}
