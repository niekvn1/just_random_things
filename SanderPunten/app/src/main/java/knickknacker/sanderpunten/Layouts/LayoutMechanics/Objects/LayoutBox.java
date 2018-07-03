package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;
import knickknacker.tcp.Networking.ConcurrentList;


/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutBox {
    protected LayoutBox parent;
    protected float left, right, bottom, top;
    protected float rLeft, rRight, rBottom, rTop;
    protected float width, height;
    protected ConcurrentList<LayoutBox> children = new ConcurrentList<>();
    protected float[] transformMatrix = null;
    protected int backgroundTexture = -1;
    protected float[] color = null;
    protected boolean relative = false;
    protected Drawable drawable = null;
    protected ArrayList<Drawable> edges = null;
    protected LayoutManager manager = null;
    protected TouchCallback touch;
    protected String id = "";
    protected float zIndex = 1.0f;

    public LayoutBox(LayoutManager manager, LayoutBox parent) {
        this.manager = manager;
        this.parent = parent;
        if (parent != null) {
            this.zIndex = parent.getzIndex() - 0.0001f;
            this.parent.addChild(this);
        }
    }

    public LayoutBox(LayoutManager manager, LayoutBox parent, float width, float height, boolean relative) {
        this(manager, parent, 0f, width , 0f, height, relative);
    }

    public LayoutBox(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        this(manager, parent);
        this.relative = relative;
        if (!relative) {
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
        } else {
            this.rLeft = left;
            this.rRight = right;
            this.rBottom = bottom;
            this.rTop = top;
        }

        this.width = this.right - this.left;
        this.height = this.top - this.bottom;
    }

    public void initAll() {
        init();
        initChilderen();
    }

    public void initAll(float left, float right, float bottom, float top) {
        init(left, right,bottom, top);
        initChilderen();
    }

    public void init() {
        if (relative) {
            float pwidth = parent.getWidth();
            float pheight = parent.getHeight();
            float pleft = parent.getLeft();
            float pbottom = parent.getBottom();

            left = pleft + rLeft * pwidth;
            right = pleft + rRight * pwidth;
            bottom = pbottom + rBottom * pheight;
            top = pbottom + rTop * pheight;
        }

        this.width = this.right - this.left;
        this.height = this.top - this.bottom;
    }

    public void initChilderen() {
        for (LayoutBox child : children.getCopy()) {
            child.initAll();
        }
    }

    public void init(float width, float height) {
        init(0f, width , 0f, height);
    }

    public void init(float left, float right, float bottom, float top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        init();
    }

    public ArrayList<Drawable> toDrawable(boolean edges) {
        ArrayList<Drawable> returnDrawables = new ArrayList<>();
        if (drawable == null) {
            if (backgroundTexture != -1 || color != null) {
                drawable = new TriangleStrip(DrawObjects.getBackgroundPoints(this.getCorners(), zIndex),
                                             color, backgroundTexture,
                                             DrawObjects.get_background_texcoords());
                drawable.setTransformMatrix(transformMatrix);
                drawable.setReady(true);
            }
        } else {
            drawable.editPoints(DrawObjects.getBackgroundPoints(this.getCorners(), zIndex));
        }

        returnDrawables.add(drawable);

        if (edges) {
            edgesToDrawables(returnDrawables);
        }

        return returnDrawables;
    }

    public void onTouchEvent(TouchData data) {
        if (data.getType() == TouchListener.TOUCH_DOWN && data.getPointerCount() == 1) {
            if (touchHit(data.getX(0), data.getY(0))) {
                onTouchDown(data);
                onTouchEventChildren(data);
            }
        } else if (data.getType() == TouchListener.TOUCH_UP && data.getPointerCount() == 0) {
            if (touchHit(data.getX(), data.getY())) {
                onTouchUp(data);
                onTouchEventChildren(data);
            }
        } else if (data.getType() == TouchListener.TOUCH_CANCEL) {
            onTouchCancel(data);
            onTouchEventChildren(data);
        }
    }

    protected void onTouchDown(TouchData data) {
        // Do things ...
    }

    protected void onTouchUp(TouchData data) {
        if (touch != null) {
            touch.onTouch(this);
        }
    }

    protected void onTouchCancel(TouchData data) {
        // Do Things ...
    }

    protected void onTouchEventChildren(TouchData data) {
        for (LayoutBox child : children.getCopy()) {
            child.onTouchEvent(data);
        }
    }

    protected boolean touchHit(float x_, float y_) {
        float x = x_ * manager.getWidth();
        float y = y_ * manager.getHeight();
        if (x >= left && x <= right && y >= bottom && y <= top) {
            return true;
        } else {
            return false;
        }
    }

    private void edgesToDrawables(ArrayList<Drawable> returnDrawables) {
        float[][] edges = getEdges(5f);
        TriangleStrip edge_stip;
        if (this.edges == null) {
            this.edges = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                edge_stip = new TriangleStrip(edges[i], Colors.RED, -1, null);
                edge_stip.setTransformMatrix(transformMatrix);
                edge_stip.setReady(true);
                this.edges.add(edge_stip);
                returnDrawables.add(edge_stip);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                this.edges.get(i).editPoints(edges[i]);
                returnDrawables.add(this.edges.get(i));
            }
        }
    }

    public void addChild(LayoutBox child) {
        children.add(child);
    }

    public float[] getTransformMatrix() {
        return transformMatrix;
    }

    public int getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(int backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float[] getCorners() {
        return new float[] {left, right, bottom, top};
    }

    public ArrayList<LayoutBox> getChildren() {
        return children.getCopy();
    }

    public float[] getColor() {
        return color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getTop() {
        return top;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public void setTouchCallback(TouchCallback touch) {
        this.touch = touch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Edge function are for testing, they are used to draw edges around boxes.
     * */
    private float[] getEdge(float left, float right, float bottom, float top) {
        return new float[] {left, bottom, zIndex, left, top, zIndex, right, bottom, zIndex, right, top, zIndex};
    }

    private float[][] getEdges(float thickness) {
        float[][] edges = new float[4][4 * 3]; // edges * points_per_edge * floats_per_point
        edges[0] = getEdge(left, left + thickness, bottom, top);
        edges[1] = getEdge(left, right, top - thickness, top);
        edges[2] = getEdge(right - thickness, right, bottom, top);
        edges[3] = getEdge(left, right, bottom, bottom + thickness);

        return edges;
    }

    public float getzIndex() {
        return zIndex;
    }

    public void setzIndex(float zIndex) {
        this.zIndex = zIndex;
    }
}
