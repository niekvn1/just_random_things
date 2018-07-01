package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

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

    public static float[] getBackgroundPoints(float width, float height, float z) {
        float[] points = {
                0.0f, 0.0f, z,
                0.0f, height, z,
                width, 0.0f, z,
                width, height, z
        };

        return points;
    }

    public static float[] getBackgroundPoints(float left, float right, float bottom, float top, float z) {
        float[] points = {
                left, bottom, z,
                left, top, z,
                right, bottom, z,
                right, top, z
        };

        return points;
    }

    public static float[] getBackgroundPoints(float[] corners, float z) {
        float[] points = {
                corners[0], corners[2], z,
                corners[0], corners[3], z,
                corners[1], corners[2], z,
                corners[1], corners[3], z
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
