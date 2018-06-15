package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchCallback;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener.TouchData;


/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutBox {
    protected LayoutBox parent;
    protected float left, right, bottom, top;
    protected float rLeft, rRight, rBottom, rTop;
    protected float width, height;
    protected ArrayList<LayoutBox> childeren = new ArrayList<>();
    protected float[] transformMatrix = null;
    protected int backgroundTexture = -1;
    protected float[] color = null;
    protected boolean relative = false;
    protected Drawable drawable = null;
    protected ArrayList<Drawable> edges = null;
    protected LayoutManager manager = null;
    protected TouchCallback touch;
    protected String id = "";

    public LayoutBox(LayoutManager manager, LayoutBox parent) {
        this.manager = manager;
        this.parent = parent;
        if (parent != null) {
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
        for (LayoutBox child : childeren) {
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

    public void newResolution(float width, float height) {
        newResolution(0f, width , 0f, height);
    }

    public void newResolution(float left, float right, float bottom, float top) {
        if (!relative) {
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
        } else {
            rLeft = left;
            rRight = right;
            rBottom = bottom;
            rTop = top;
        }

        resolutionChain();
    }

    protected void resolutionChain() {
        resolutionMe();
        resolutionChilderen();
    }

    protected void resolutionMe() {
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
        System.out.println("Resolution Chain. " + left + " " + right + " " + bottom + " " + top + " " + width + " " + height);

        if (edges == null) {
            toDrawable(false);
        } else {
            toDrawable(true);
        }
    }

    protected void resolutionChilderen() {
        for (LayoutBox child : childeren) {
            child.resolutionChain();
        }
    }

    public ArrayList<Drawable> toDrawable(boolean edges) {
        ArrayList<Drawable> returnDrawables = new ArrayList<>();
        if (drawable == null) {
            if (backgroundTexture != -1 || color != null) {
                    drawable = new TriangleStrip(DrawObjects.getBackgroundPoints(this.getCorners()),
                                                   color, backgroundTexture,
                                                   DrawObjects.get_background_texcoords());
                    drawable.setTransformMatrix(transformMatrix);
                    drawable.setReady(true);
                    returnDrawables.add(drawable);
            }
        } else {
            drawable.editPoints(DrawObjects.getBackgroundPoints(this.getCorners()));
        }

        if (edges) {
            edgesToDrawables(returnDrawables);
        }

        return returnDrawables;
    }

    public void onTouchEvent(TouchData data) {
        if (data.getType() == TouchListener.TOUCH_DOWN && data.getPointerCount() == 1) {
            if (touchHit(data.getX(0), data.getY(0))) {
                onTouchDown(data);
                onTouchEventChilderen(data);
            }
        } else if (data.getType() == TouchListener.TOUCH_UP && data.getPointerCount() == 0) {
            if (touchHit(data.getX(), data.getY())) {
                onTouchUp(data);
                onTouchEventChilderen(data);
            }
        } else if (data.getType() == TouchListener.TOUCH_CANCEL) {
            onTouchCancel(data);
            onTouchEventChilderen(data);
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

    protected void onTouchEventChilderen(TouchData data) {
        for (LayoutBox child : childeren) {
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
            }
        }
    }

    public void addChild(LayoutBox child) {
        childeren.add(child);
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

    public ArrayList<LayoutBox> getChilderen() {
        return childeren;
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
        return new float[] {left, bottom, left, top, right, bottom, right, top};
    }

    private float[][] getEdges(float thickness) {
        float[][] edges = new float[4][4 * 2]; // edges * points_per_edge * floats_per_point
        edges[0] = getEdge(left, left + thickness, bottom, top);
        edges[1] = getEdge(left, right, top - thickness, top);
        edges[2] = getEdge(right - thickness, right, bottom, top);
        edges[3] = getEdge(left, right, bottom, bottom + thickness);

        return edges;
    }
}
