package knickknacker.sanderpunten.Drawing.Objects;

import java.util.ArrayList;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutBox {
    private LayoutBox parent;
    protected float left, right, bottom, top;
    protected float width, height;
    private ArrayList<LayoutBox> childeren = new ArrayList<>();
    private float[] transformMatrix;
    private int backgroundTexture = -1;

    public LayoutBox(LayoutBox parent) {
        this.parent = parent;
        this.parent.addChild(this);
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

        this.width = right - left;
        this.height = top - bottom;

        if (parent == null) {
            transformMatrix = null;
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

    public float[] getCorners() {
        return new float[] {left, right, bottom, top};
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
