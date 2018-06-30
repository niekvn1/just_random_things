package knickknacker.sanderpunten.Rendering.Drawing.Drawables;

import android.opengl.GLES20;

/**
 * Created by Niek on 22-5-2018.
 *
 * A child class of Drawable, used to draw Triangle Strips with OpenGL ES 2.0.
 */

public class TriangleStrip extends Drawable {
    public TriangleStrip(float[] points, float[] color, int texture, float[] texcoords) {
        super(points, color, texture, texcoords, GLES20.GL_TRIANGLE_STRIP);
    }
}
