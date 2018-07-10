package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchSubscriber;

/**
 * Created by Niek on 29-5-2018.
 */

public class Button extends LayoutBox implements TouchSubscriber {
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
        layout.subscribeToTouch(this);
        if (drawable != null) {
            drawable.editColor(hitColor);
        }

        super.onTouchDown(data);
    }

    @Override
    public void onTouchUp(TouchData data) {
        down = false;
        drawable.editColor(color);
        super.onTouchUp(data);
    }

    private void onTouchUpSub(TouchData data) {
        if (!touchHit(data.getX(), data.getY())) {
            onTouchCancel();
        }
    }



    private void onTouchCancel() {
        if (down) {
            drawable.editColor(color);
            down = false;
        }

        layout.unsubscripeToTouch(this);
    }

    public void onTouchSub(TouchData data) {
        switch(data.getType()) {
            case TouchListener.TOUCH_UP:
                onTouchUpSub(data);
                break;
            case TouchListener.TOUCH_CANCEL:
                onTouchCancel();
                break;
        }
    }

    public float[] getHitColor() {
        return hitColor;
    }

    public void setHitColor(float[] hitColor) {
        this.hitColor = hitColor;
    }
}
