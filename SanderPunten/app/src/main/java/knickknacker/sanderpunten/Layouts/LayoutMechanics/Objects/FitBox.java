package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

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

    public FitBox(LayoutManager manager, LayoutBox parent, boolean fit) {
        super(manager, parent);
        this.fit = fit;
    }

    public FitBox(LayoutManager manager, LayoutBox parent, float width, float height, boolean relative, boolean fit) {
        this(manager, parent, 0f, width , 0f, height, relative, fit);
    }

    public FitBox(LayoutManager manager, LayoutBox parent, float left_, float right_, float bottom_, float top_, boolean relative, boolean fit) {
        super(manager, parent, left_, right_, bottom_, top_, relative);
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
        float margin = manager.getUnit() * childMargin;
        float button_height = (height - (childCount - 1) * margin) / childCount;
        for (int i = 0; i < childCount; i++) {
            float[] corners = new float[4];
//                left, right, bottom, top
            corners[0] = left;
            corners[1] = right;
            corners[2] = top - i * margin - (i + 1) * button_height;
            corners[3] = top - i * margin - i * button_height;

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
