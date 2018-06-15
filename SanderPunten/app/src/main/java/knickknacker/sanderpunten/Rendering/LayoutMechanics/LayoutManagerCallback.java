package knickknacker.sanderpunten.Rendering.LayoutMechanics;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 29-5-2018.
 */

public interface LayoutManagerCallback {
    void setupLayout(LayoutBox root);

    void loadLayout(float unit);
}
