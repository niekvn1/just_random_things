package knickknacker.sanderpunten.Layouts;

import java.util.ArrayList;

import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchSubscriber;
import knickknacker.opengldrawables.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 2-6-2018.
 *
 * This class holds the root of a layout tree node. It contains functions to manage the
 * responsiveness of the layout and to create and update the drawables of the LayoutBoxes.
 */

public class Layout {
    private LayoutBox root;
    private LayoutBox touchRoot;
    private ArrayList<Drawable> drawables;
    private boolean initialized = false;
    private boolean drawInitialized = false;
    private boolean edges;
    private boolean used = false;
    private LayoutManager manager;
    private ArrayList<TouchSubscriber> touchSubscribers;

    public Layout(LayoutManager manager, boolean edges) {
        this.manager = manager;
        this.root = new LayoutBox(this);
        touchRoot = root;
        drawables = new ArrayList<>();
        touchSubscribers = new ArrayList<>();
        this.edges = edges;
    }


    public void init(float width, float height) {
        /** Initialize the layout with a width and a height, this will start a traversal of the
         * LayoutBox tree, editing their position points. */
        root.init(width, height);
        root.initChilderen();
    }

    public void initDrawables() {
        initDrawables(initialized);
    }

    public ArrayList<Drawable> initDrawables(boolean all) {
        ArrayList<Drawable> collection = new ArrayList<>();
        initDrawables(collection, root, all);
        if (!initialized) {
            initialized = true;
            drawables = collection;
        }

        return collection;
    }

    private void initDrawables(ArrayList<Drawable> collection, LayoutBox layoutBox, boolean all) {
        /** Traverse the LayoutBox tree and initialize their drawable objects. */
        ArrayList<Drawable> drawables = layoutBox.toDrawable(edges);
        for (Drawable d : drawables) {
            if (d != null && (all || !this.drawables.contains(d))) {
                collection.add(d);
            }
        }

        for (LayoutBox child : layoutBox.getChildren()) {
            initDrawables(collection, child, all);
        }
    }

    public LayoutBox getRoot() {
        /** Returns the root of the LayoutBox tree. */
        return root;
    }

    public ArrayList<Drawable> getDrawables() {
        /** Get all the drawables of this layout. */
        return drawables;
    }

    public boolean isDrawInitialized() {
        /** Check if this layout has been initialized. */
        return drawInitialized;
    }

    public void setDrawInitialized(boolean drawInitialized) {
        /** Tell the layout that all the drawables are initialized by the GLRenderer. */
        this.drawInitialized = drawInitialized;
    }

    public boolean isInitialized() {
        /** Check if this layouts drawables have been initialized by the GLRenderer. */
        return initialized;
    }

    public boolean isUsed() {
        /** Check if the layout is being used. */
        return used;
    }

    public void setUsed(boolean used) {
        /** Set used. */
        this.used = used;
    }

    public LayoutManager getManager() {
        return manager;
    }

    public void setManager(LayoutManager manager) {
        this.manager = manager;
    }

    public void reload() {
        manager.reload(this);
    }

    public void onTouch(TouchListener.TouchData data) {
        touchRoot.onTouchEvent(data);
        for (TouchSubscriber sub : touchSubscribers) {
            sub.onTouchSub(data);
        }
    }

    public void setTouchRoot(LayoutBox touchRoot) {
        this.touchRoot = touchRoot;
    }

    public void subscribeToTouch(TouchSubscriber sub) {
        touchSubscribers.add(sub);
    }

    public void unsubscripeToTouch(TouchSubscriber sub) {
        touchSubscribers.remove(sub);
    }
}
