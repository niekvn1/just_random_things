package knickknacker.sanderpunten.Drawing.Objects;

import java.util.ArrayList;

/**
 * Created by Niek on 22-5-2018.
 */

public abstract class DrawObjects {
    public static float[] get_background_points() {
        float[] points = {
                -1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f
        };

        return points;
    }

    public static float[] getBackgroundPoints(float width, float height) {
        float[] points = {
                0.0f, 0.0f,
                0.0f, height,
                width, 0.0f,
                width, height
        };

        return points;
    }

    public static float[] getBackgroundPoints(float left, float right, float bottom, float top) {
        float[] points = {
                left, bottom,
                left, top,
                right, bottom,
                right, top
        };

        return points;
    }

    public static float[] getBackgroundPoints(float[] corners) {
        float[] points = {
                corners[0], corners[2],
                corners[0], corners[3],
                corners[1], corners[2],
                corners[1], corners[3]
        };

        return points;
    }

    public static float[] get_background_texcoords() {
        float[] points = {
                0f, 1f,
                0f, 0f,
                1f, 1f,
                1f, 0f
        };

        return points;
    }
}
