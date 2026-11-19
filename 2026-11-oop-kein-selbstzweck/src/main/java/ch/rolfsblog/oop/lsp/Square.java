package ch.rolfsblog.oop.lsp;

/**
 * A square as a subclass of {@link Rectangle}: the classic violation of the
 * Liskov substitution principle. To keep its own invariant (equal sides),
 * each setter changes both sides and thereby breaks the contract of the
 * rectangle's setters.
 */
public class Square extends Rectangle {

    /**
     * Creates a square.
     *
     * @param side the length of each side
     */
    public Square(int side) {
        super(side, side);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(int height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
