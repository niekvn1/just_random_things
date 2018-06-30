package knickknacker.sanderpunten.Rendering.LayoutMechanics;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 29-5-2018.
 *
 * Functions a LayoutManagerCallback should implements.
 */

public interface LayoutManagerCallback {

    /** Setup for the layout. */
    void setupLayout(LayoutBox root);

    /** Load the layout now the unit is available. */
    void loadLayout(float unit);
}
