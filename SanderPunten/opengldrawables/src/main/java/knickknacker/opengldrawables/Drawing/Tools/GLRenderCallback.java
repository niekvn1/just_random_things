package knickknacker.opengldrawables.Drawing.Tools;

/**
 * Created by Niek on 25-5-2018.
 *
 * Functions a GLRenderCallback should implement.
 */

public interface GLRenderCallback {

    /** Callback for when the surface is created. */
    void surfaceCreatedCallback();

    /** Callback for when the surface is changed. */
    void surfaceChangedCallback(int width, int height);

    /** Callback for every onDrawFrame. */
    void onDrawCallback();
}
