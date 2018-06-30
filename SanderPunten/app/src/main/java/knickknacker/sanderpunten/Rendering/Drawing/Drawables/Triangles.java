package knickknacker.sanderpunten.Rendering.Drawing.Drawables;

import android.opengl.GLES20;

/**
 * Created by Niek on 25-5-2018.
 *
 * A child class of Drawable, used to draw Triangles with OpenGL ES 2.0.
 */

public class Triangles extends Drawable {
    public Triangles(float[] points, float[] color, int texture, float[] texcoords) {
        super(points, color, texture, texcoords, GLES20.GL_TRIANGLES);
    }
}
