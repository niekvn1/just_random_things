package knickknacker.sanderpunten.Rendering;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 2-6-2018.
 */

public class Layout {
    private LayoutBox root;
    private ArrayList<Drawable> drawables;
    private boolean drawInitialized = false;

    public Layout(LayoutManager manager) {
        this.root = new LayoutBox(manager, null);
        drawables = new ArrayList<>();
    }

    public LayoutBox getRoot() {
        return root;
    }

    public void setRoot(LayoutBox root) {
        this.root = root;
    }

    public ArrayList<Drawable> getDrawables() {
        return drawables;
    }

    public void setDrawables(ArrayList<Drawable> drawables) {
        this.drawables = drawables;
    }

    public void addDrawable(Drawable d) {
        drawables.add(d);
    }

    public boolean isDrawInitialized() {
        return drawInitialized;
    }

    public void setDrawInitialized(boolean drawInitialized) {
        this.drawInitialized = drawInitialized;
    }
}
