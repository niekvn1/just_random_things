package knickknacker.sanderpunten.Drawing.Objects;

import java.util.ArrayList;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutBox {
    protected LayoutBox parent;
    protected float left, right, bottom, top;
    protected float width, height;
    protected ArrayList<LayoutBox> childeren = new ArrayList<>();
    protected float[] transformMatrix = null;
    protected int backgroundTexture = -1;

    public LayoutBox(LayoutBox parent) {
        this.parent = parent;
        if (parent != null) {
            this.parent.addChild(this);
        }
    }

    public LayoutBox(LayoutBox parent, float width, float height) {
        this(parent, 0f, width , 0f, height);
    }

    public LayoutBox(LayoutBox parent, float left, float right, float bottom, float top) {
        this(parent);
        newResolution(left, right, bottom, top);
    }

    public void newResolution(float width, float height) {
        newResolution(0f, width , 0f, height);
    }

    public void newResolution(float left, float right, float bottom, float top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;

        this.width = this.right - this.left;
        this.height = this.top - this.bottom;
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

    /**
     * Edge function are for testing, they are used to draw edges around boxes.
     * */
    private float[] getEdge(float left, float right, float bottom, float top) {
        return new float[] {left, bottom, left, top, right, bottom, right, top};
    }

    public float[][] getEdges(float thickness) {
        float[][] edges = new float[4][4 * 2]; // edges * points_per_edge * floats_per_point
        edges[0] = getEdge(left, left + thickness, bottom, top);
        edges[1] = getEdge(left, right, top - thickness, top);
        edges[2] = getEdge(right - thickness, right, bottom, top);
        edges[3] = getEdge(left, right, bottom, bottom + thickness);

        return edges;
    }
}
