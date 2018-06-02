package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;

/**
 * Created by Niek on 2-6-2018.
 */

public class FitBox extends LayoutBox {
    private float childMargin;
    private float childSize;
    private int childCount;
    private boolean childrenSet = false;
    private boolean fit;
    float[] childColor;
    int childTexture = -1;

    public FitBox(LayoutManager manager, LayoutBox parent, float left_, float right_, float bottom_, float top_, boolean relative, boolean fit, int childCount) {
        super(manager, parent, left_, right_, bottom_, top_, relative);
        this.fit = fit;
        this.childCount = childCount;
        for (int i = 0; i < this.childCount; i++) {
            createChild();
        }
    }

    @Override
    public void initAll() {
        super.init();
        if (fit) {
            exactFit();
        }
    }

    @Override
    protected void resolutionChain() {
        super.resolutionMe();
        exactFit();
        super.resolutionChilderen();
    }

    protected void createChild() {
        new LayoutBox(manager, this);
    }

    protected void initChild(int i, float[] corners) {
        LayoutBox layoutBox = childeren.get(i);
        layoutBox.setColor(childColor);
        layoutBox.setBackgroundTexture(childTexture);
        layoutBox.initAll(corners[0], corners[1], corners[2], corners[3]);
    }

    private void exactFit() {
        float margin = manager.getUnit() * childMargin;
        float button_height = (height - (childCount - 1) * margin) / childCount;
        for (int i = 0; i < childCount; i++) {
            float[] corners = new float[4];
//                left, right, bottom, top
            corners[0] = left;
            corners[1] = right;
            corners[2] = top - i * margin - (i + 1) * button_height;
            corners[3] = top - i * margin - i * button_height;

            if (!childrenSet) {
                initChild(i, corners);
            } else {
                childeren.get(i).newResolution(corners[0], corners[1], corners[2], corners[3]);
            }
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

    public void setChildColor(float[] childColor) {
        this.childColor = childColor;
    }

    public void setChildTexture(int childTexture) {
        this.childTexture = childTexture;
    }
}
