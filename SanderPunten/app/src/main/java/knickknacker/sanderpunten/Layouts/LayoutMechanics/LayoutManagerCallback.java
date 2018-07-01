package knickknacker.sanderpunten.Layouts.LayoutMechanics;

import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 29-5-2018.
 *
 * Functions a LayoutManagerCallback should implements.
 */

public interface LayoutManagerCallback {

    /** Setup for the layout. */
    void setupLayout(LayoutBox root);
}
