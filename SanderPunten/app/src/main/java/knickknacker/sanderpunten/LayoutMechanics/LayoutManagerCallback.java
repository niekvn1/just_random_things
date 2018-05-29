package knickknacker.sanderpunten.LayoutMechanics;

import knickknacker.sanderpunten.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Drawing.Tools.TextManager;

/**
 * Created by Niek on 29-5-2018.
 */

public interface LayoutManagerCallback {
    void surfaceCreatedCallback(LayoutBox root, TextManager textManager);
}
