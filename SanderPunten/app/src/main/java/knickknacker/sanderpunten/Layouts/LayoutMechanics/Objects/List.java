package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;

public class List extends LayoutBox {
    private int childCount = 0;
    private boolean center = true;
    private float margin = 0f;
    private float childMargin = 0f;

    public List(LayoutManager layoutManager, LayoutBox parent) {
        super(layoutManager, parent);
    }

    public List(LayoutManager manager, LayoutBox parent, float width, float height, boolean relative) {
        this(manager, parent, 0f, width , 0f, height, relative);
    }

    public List(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(manager, parent, left, right, bottom, top, relative);
    }

    @Override
    public void addChild(LayoutBox box) {
        super.addChild(box);
        childCount++;
    }

    @Override
    public void initAll() {
        super.init();
        handle_children();
    }

    private void handle_children() {
        float boxWidth;
        float boxHeight;
        float[] corners = new float[4];
        float offset = top;
        for (LayoutBox box : children.getCopy()) {
            boxWidth = box.getWidth();
            boxHeight = box.getHeight();
            if (center) {
                corners[0] = left + (width - boxWidth) / 2;
                corners[1] = right - (width - boxWidth) / 2;
            }

            corners[2] = offset - boxHeight - margin;
            corners[3] = offset - margin;
            Log.i("CORNERS", corners[0] + " " + corners[1] + " " + corners[2] + " " + corners[3] + " " + boxWidth + " " + boxHeight);

            offset = corners[2];
            box.initAll(corners[0], corners[1], corners[2], corners[3]);
        }
    }

    public int getChildCount() {
        return childCount;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public void setChildMargin(float childMargin) {
        this.childMargin = childMargin;
    }
}
