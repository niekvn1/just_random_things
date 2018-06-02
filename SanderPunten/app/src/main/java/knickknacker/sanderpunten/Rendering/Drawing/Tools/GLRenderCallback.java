package knickknacker.sanderpunten.Rendering.Drawing.Tools;

/**
 * Created by Niek on 25-5-2018.
 */

public interface GLRenderCallback {
    void surfaceCreatedCallback();

    void surfaceChangedCallback(int width, int height);
}
