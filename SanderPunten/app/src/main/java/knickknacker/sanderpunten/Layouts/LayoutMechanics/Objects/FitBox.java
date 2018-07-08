package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;

/**
 * Created by Niek on 2-6-2018.
 */

public class FitBox extends LayoutBox {
    private float childMargin;
    private float childSize;
    private int childCount;
    private boolean childrenSet = false;
    private boolean fit;

    public FitBox(Layout layout) {
        super(layout);
    }

    public FitBox(LayoutBox parent, boolean fit) {
        super(parent);
        this.fit = fit;
    }

    public FitBox(LayoutBox parent, float width, float height, boolean relative, boolean fit) {
        this(parent, 0f, width , 0f, height, relative, fit);
    }

    public FitBox(LayoutBox parent, float left_, float right_, float bottom_, float top_, boolean relative, boolean fit) {
        super(parent, left_, right_, bottom_, top_, relative);
        this.fit = fit;
    }

    @Override
    public void addChild(LayoutBox box) {
        super.addChild(box);
        childCount++;
    }

    @Override
    public void initAll() {
        super.init();
        if (fit) {
            exactFit();
        }
    }

    protected void initChild(int i, float[] corners) {
        LayoutBox layoutBox = children.get(i);
        layoutBox.initAll(corners[0], corners[1], corners[2], corners[3]);
    }

    public void exactFit() {
        float margin = childMargin;
        float button_height = (getUnitHeight() - (childCount - 1) * margin) / childCount;
        Log.i("Height", ""+ button_height);
        for (int i = 0; i < childCount; i++) {
            float[] corners = new float[4];
//                left, right, bottom, top
            corners[0] = getUnitLeft();
            corners[1] = getUnitRight();
            corners[2] = getUnitTop() - i * margin - (i + 1) * button_height;
            corners[3] = getUnitTop() - i * margin - i * button_height;

            initChild(i, corners);
        }

        childrenSet = true;
    }

    public float getChildMargin() {
        return childMargin;
    }

    public void setChildMargin(float childMargin) {
        this.childMargin = childMargin;
    }

    public float getChildSize() {
        return childSize;
    }

    public void setChildSize(float childSize) {
        this.childSize = childSize;
    }

    public int getChildCount() {
        return childCount;
    }
}
