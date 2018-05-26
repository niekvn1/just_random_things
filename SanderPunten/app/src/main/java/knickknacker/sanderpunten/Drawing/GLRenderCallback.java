package knickknacker.sanderpunten.Drawing;

/**
 * Created by Niek on 25-5-2018.
 */

public interface GLRenderCallback {
    void surfaceCreatedCallback();

    void surfaceChangedCallback(int width, int height);
}
