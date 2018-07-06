package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;

import java.util.ArrayList;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchSubscriber;

public class List extends LayoutBox implements TouchSubscriber {
    private int childCount = 0;
    private float totalChildHeight = 0f;
    private boolean center = true;
    private boolean scrollOnNewChild = true;
    private float margin = 0f;
    private float prev_y = -1f;
    private int offsetChild = -1;
    private int offsetChildHeightHidden = -1;
    private float scrollOffset = 0;
    private float diffY;
    private boolean atTop;
    private boolean atBottom;

    public List(Layout layout) {
        super(layout);
        layout.subscribeToTouch(this);
    }

    public List(LayoutBox parent) {
        super(parent);
        layout.subscribeToTouch(this);
    }

    public List(LayoutBox parent, float width, float height, boolean relative) {
        this(parent, 0f, width , 0f, height, relative);
    }

    public List(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(parent, left, right, bottom, top, relative);
        layout.subscribeToTouch(this);
    }

    @Override
    public void addChild(LayoutBox box) {
        if (box.isRelative()) {
            Log.e("List", "Cannot add relative LayoutBox to List.");
            return;
        }

        super.addChild(box);
        if (scrollOffset > 0) {
            scrollOffset = 0;
            offsetChild = children.size() - 1;
        }

        childCount++;
        totalChildHeight += box.getHeight();

        box.setIgnore(true);
    }

    @Override
    public void initAll() {
        super.init();
        if (offsetChild == -1) {
            offsetChild = children.size() - 1;
            offsetChildHeightHidden = 0;
            if (totalChildHeight + childCount * margin > height) {
                atTop = false;
                atBottom = true;
            } else {
                atTop = true;
                atBottom = true;
            }
        }

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

//        Log.i("CORNERS", corners[0] + " " + corners[1] + " " + corners[2] + " " + corners[3] + " " + boxWidth + " " + boxHeight);
    }

    private void topDown() {
//        Log.i("List", "TopDown");
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
//        Log.i("BUG", "BottomUp begin");
        float[] corners = new float[4];
        float offset = bottom + scrollOffset;
        ArrayList<LayoutBox> copy = children.getCopy();
        LayoutBox box;
        boolean full = false;

        if (copy.size() == 0) {
            return;
        }

        for (int i = offsetChild; i >= 0; i--) {
            box = copy.get(i);

            getChildCorners(corners, offset, box, true);

            if (i == 0 && corners[3] < top) {
                atTop = true;
            } else if (atTop) {
                atTop = false;
            }

            if (i == offsetChild && diffY < 0 && corners[3] < bottom && !box.isIgnore()) {
                layout.getManager().toIgnore(box);
                offsetChild -= 1;
                offset = bottom + diffY;
                scrollOffset = 0;
            } else if (i == offsetChild && diffY > 0 && i + 1 < copy.size() && copy.get(i + 1).isIgnore() && corners[2] > bottom + margin) {
                offsetChild += 1;
                scrollOffset -= (copy.get(offsetChild).getHeight() + margin);
                offset = load(box, corners);
            } else if (full && !box.isIgnore()) {
//                Log.i("LIST", "top ignore");
                layout.getManager().toIgnore(box);
            } else if (full && box.isIgnore()) {
//                Log.i("LIST", "return at " + i);
                return;
            } else if (corners[3] > top) {
//                Log.i("LIST", "set full");
                full = true;
                offset = load(box, corners);
            } else {
//                Log.i("LIST", "not full");
                offset = load(box, corners);
            }
        }
    }

    private float load(LayoutBox box, float[] corners) {
        /** Set the new corners after scrolling for a box and return the offset for the next box. */
        box.setIgnore(false);
        box.initAll(corners[0], corners[1], corners[2], corners[3]);
        return corners[3];
    }

    private void scroll() {
        if (totalChildHeight + childCount * margin > height && !(atTop && diffY < 0)) {
//            Log.i("DiffY", "" + diffY + " offset: " + scrollOffset + " child: " + offsetChild);
            scrollOffset += diffY;
            layout.reload();
//            Log.i("BUG:", "set offset");
        }
    }

    public void onTouchSub(TouchData data) {
        switch (data.getType()) {
            case TouchListener.TOUCH_DOWN:
                onTouchDownSub(data);
                break;
            case TouchListener.TOUCH_UP:
                onTouchUpSub(data);
                break;
        }
    }

    public void onTouchDownSub(TouchData data) {
        prev_y = data.getY();
    }

    public void onTouchUpSub(TouchData data) {
        prev_y = -1;
    }

    @Override
    public void onTouchMove(TouchData data) {
        if (prev_y < 0) {
            prev_y = data.getY();
            return;
        }

        diffY = (data.getY() - prev_y) * 1000;
        scroll();

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
