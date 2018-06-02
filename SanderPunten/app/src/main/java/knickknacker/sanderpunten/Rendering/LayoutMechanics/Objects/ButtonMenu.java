package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;

/**
 * Created by Niek on 22-5-2018.
 */

public class ButtonMenu extends FitBox {
    public ButtonMenu(LayoutManager manager, LayoutBox parent, float left_, float right_, float bottom_, float top_, boolean relative, boolean fit, int buttonCount) {
        super(manager, parent, left_, right_, bottom_, top_, relative, fit, buttonCount);
    }


    @Override
    protected void createChild() {
        new Button(manager, this);
    }
}
