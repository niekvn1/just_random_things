package knickknacker.sanderpunten.Rendering.Drawing.Drawables;

/**
 * Created by Niek on 23-5-2018.
 *
 * A child class of Drawable/Triangles, used to draw text with OpenGL ES 2.0.
 */

public class Text extends Triangles {
    public Text(float[] points, float[] color, int texture, float[] texcoords) {
        super(points, color, texture, texcoords);
    }
}
