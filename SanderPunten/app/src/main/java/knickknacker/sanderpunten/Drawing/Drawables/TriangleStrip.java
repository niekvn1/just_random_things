package knickknacker.sanderpunten.Drawing.Drawables;

import android.opengl.GLES20;

import knickknacker.sanderpunten.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Drawing.Tools.Matrices;
import knickknacker.sanderpunten.Drawing.Tools.Shaders;

/**
 * Created by Niek on 22-5-2018.
 */

public class TriangleStrip extends Vertices {
    public TriangleStrip(float[] points, float[] color, int texture, float[] texcoords) {
        super(points, color, texture, texcoords, GLES20.GL_TRIANGLE_STRIP);
    }
}
