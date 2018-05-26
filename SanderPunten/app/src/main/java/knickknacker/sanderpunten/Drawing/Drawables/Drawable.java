package knickknacker.sanderpunten.Drawing.Drawables;

/**
 * Created by Niek on 22-5-2018.
 */

public interface Drawable {
    void draw();

    void init();

    int getTexture();

    void setTexture(int texture);

    void setProgramHandle(int handle);

    void setTransformMatrix(float[] transform);
}
