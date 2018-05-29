package knickknacker.sanderpunten.LayoutMechanics.Objects;

/**
 * Created by Niek on 29-5-2018.
 */

public class Button extends LayoutBox {
    public Button(LayoutBox parent) {
        super(parent);
    }

    public Button(LayoutBox parent, float width, float height, boolean relative) {
        super(parent, 0f, width , 0f, height, relative);
    }

    public Button(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(parent, left, right, bottom, top, relative);
    }
}
