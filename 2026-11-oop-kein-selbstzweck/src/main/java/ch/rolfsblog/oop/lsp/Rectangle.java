package ch.rolfsblog.oop.lsp;

/**
 * A mutable rectangle.
 * Contract of {@link #setWidth(int)}: the width changes, the height stays.
 */
public class Rectangle {

    private int width;
    private int height;

    /**
     * Creates a rectangle.
     *
     * @param width the width
     * @param height the height
     */
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Changes the width. The height stays unchanged.
     *
     * @param width the new width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Changes the height. The width stays unchanged.
     *
     * @param height the new height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the area.
     *
     * @return width times height
     */
    public int area() {
        return width * height;
    }
}
