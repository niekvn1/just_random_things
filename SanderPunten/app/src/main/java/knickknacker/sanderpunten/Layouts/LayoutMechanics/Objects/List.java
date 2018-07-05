package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import java.util.ArrayList;

import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;

public class List extends LayoutBox {
    private int childCount = 0;
    private float totalChildHeight = 0f;
    private boolean center = true;
    private boolean scrollOnNewChild = true;
    private float margin = 0f;
    private float scroll = 0f;
    private float prev_y = -1f;


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
        if (box.isRelative()) {
            Log.e("List", "Cannot add relative LayoutBox to List.");
            return;
        }

        super.addChild(box);
        childCount++;
        totalChildHeight += box.getHeight();
        Log.i("List", "ChildHeight: " + totalChildHeight + " height: " + height);
        box.setIgnore(true);
    }

    @Override
    public void initAll() {
        super.init();
        handle_children();
    }

    private void getChildCorners(float[] corners, float offset, LayoutBox box, boolean bottomUp) {
        float boxWidth = box.getWidth();
        float boxHeight = box.getHeight();
        if (center) {
            corners[0] = left + (width - boxWidth) / 2;
            corners[1] = right - (width - boxWidth) / 2;
        }

        if (bottomUp) {
            corners[2] = offset + margin;
            corners[3] = offset + boxHeight + margin;
        } else {
            corners[2] = offset - boxHeight - margin;
            corners[3] = offset - margin;
        }

        Log.i("CORNERS", corners[0] + " " + corners[1] + " " + corners[2] + " " + corners[3] + " " + boxWidth + " " + boxHeight);
    }

    private void topDown() {
        Log.i("List", "TopDown");
        float[] corners = new float[4];
        float offset = top;
        for (LayoutBox box : children.getCopy()) {
            getChildCorners(corners, offset, box, false);

            if (corners[2] < bottom) {
                offset = corners[2];
                box.setIgnore(false);
                box.initAll(corners[0], corners[1], corners[2], corners[3]);
            } else {
                offset = corners[2];
                box.setIgnore(false);
                box.initAll(corners[0], corners[1], corners[2], corners[3]);
            }
        }
    }

    private void bottomUp() {
        Log.i("List", "BottomUp");
        float[] corners = new float[4];
        float offset = bottom;
        ArrayList<LayoutBox> copy = children.getCopy();
        LayoutBox box;
        boolean full = false;
        for (int i = copy.size() - 1; i >= 0; i--) {
            box = copy.get(i);
            Log.i("List", "test: " + i + " full: " + full + " ignore: " + box.isIgnore());
            getChildCorners(corners, offset, box, true);
            if (full && !box.isIgnore()) {
                manager.toIgnore(box);
            } else if (full && box.isIgnore()) {
                Log.i("List", "RETURN RETURN RETURN RETURN RETURN RETURN RETURN ");
                return;
            } else if (corners[3] > top) {
                full = true;
                box.setIgnore(false);
                box.initAll(corners[0], corners[1], corners[2], corners[3]);
            } else {
                offset = corners[3];
                box.setIgnore(false);
                box.initAll(corners[0], corners[1], corners[2], corners[3]);
            }
        }
    }

    @Override
    protected void onTouchMove(TouchData data) {
        if (prev_y < 0) {
            prev_y = data.getY();
            return;
        }

        Log.i("List", "Touch: " + data.getY() + " and " + prev_y);
        float diffY = data.getY() - prev_y;
        Log.i("DiffY", prev_y + "  " + diffY);

        prev_y = data.getY();
    }

    private void handle_children() {
        if (totalChildHeight + childCount * margin > height) {
            bottomUp();
        } else {
            topDown();
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
}
