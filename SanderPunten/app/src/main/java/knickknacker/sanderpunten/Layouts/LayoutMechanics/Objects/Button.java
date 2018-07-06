package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;

/**
 * Created by Niek on 29-5-2018.
 */

public class Button extends LayoutBox {
    private float[] hitColor = null;
    private boolean down = false;

    public Button(Layout layout) {
        super(layout);
    }

    public Button(LayoutBox parent) {
        super(parent);
    }

    public Button(LayoutBox parent, float width, float height, boolean relative) {
        super(parent, 0f, width , 0f, height, relative);
    }

    public Button(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(parent, left, right, bottom, top, relative);
    }

    @Override
    public void onTouchDown(TouchData data) {
        down = true;
        Log.i("BUTTON", "onTouchDown");
        if (drawable != null) {
            drawable.editColor(hitColor);
        }

        super.onTouchDown(data);
    }

    @Override
    public void onTouchUp(TouchData data) {
        Log.i("BUTTON", "onTouchUp");
        down = false;
        drawable.editColor(color);
        super.onTouchUp(data);
    }

    @Override
    public void onTouchCancel(TouchData data) {
        if (down) {
            drawable.editColor(color);
        }

        super.onTouchCancel(data);
    }

    public float[] getHitColor() {
        return hitColor;
    }

    public void setHitColor(float[] hitColor) {
        this.hitColor = hitColor;
    }
}
