package knickknacker.sanderpunten.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Drawing.Properties.Colors;

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
    protected Drawable background = null;
    protected ArrayList<Drawable> edges = null;

    public LayoutBox(LayoutBox parent) {
        this.parent = parent;
        if (parent != null) {
            this.parent.addChild(this);
        }
    }

    public LayoutBox(LayoutBox parent, float width, float height, boolean relative) {
        this(parent, 0f, width , 0f, height, relative);
    }

    public LayoutBox(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        this(parent);
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
        System.out.println("Resolution Chain");
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
        if (background == null) {
            if (backgroundTexture != -1 || color != null) {
                    background = new TriangleStrip(DrawObjects.getBackgroundPoints(this.getCorners()),
                                                   color, backgroundTexture,
                                                   DrawObjects.get_background_texcoords());
                    background.setTransformMatrix(transformMatrix);
                    returnDrawables.add(background);
            }
        } else {
            background.edit(DrawObjects.getBackgroundPoints(this.getCorners()), null);
        }

        if (edges) {
            edgesToDrawables(returnDrawables);
        }

        return returnDrawables;
    }

    private void edgesToDrawables(ArrayList<Drawable> returnDrawables) {
        float[][] edges = getEdges(5f);
        TriangleStrip edge_stip;
        if (this.edges == null) {
            this.edges = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                edge_stip = new TriangleStrip(edges[i], Colors.RED, -1, null);
                edge_stip.setTransformMatrix(transformMatrix);
                this.edges.add(edge_stip);
                returnDrawables.add(edge_stip);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                this.edges.get(i).edit(edges[i], null);
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
