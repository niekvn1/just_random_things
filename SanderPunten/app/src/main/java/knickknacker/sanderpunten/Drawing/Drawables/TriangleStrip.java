package knickknacker.sanderpunten.Drawing.Drawables;

import android.opengl.GLES20;

/**
 * Created by Niek on 22-5-2018.
 */

public class TriangleStrip extends Drawable {
    public TriangleStrip(float[] points, float[] color, int texture, float[] texcoords) {
        super(points, color, texture, texcoords, GLES20.GL_TRIANGLE_STRIP);
    }
}
