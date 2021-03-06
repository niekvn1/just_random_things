package knickknacker.opengldrawables.Drawing.Tools;

/**
 * Created by Niek on 22-5-2018.
 */

public abstract class Matrices {
    /** Identity matrix. */
    public static final float[] identity = {
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
    };

    public static float[] getProjectionMatrix(float screenWidth, float screenHeight, float worldWidth, float worldHeight) {
        /** Get the Projection matrix for the given widths and heights. */
        float[] scales = getGLScale(screenWidth, screenHeight, worldWidth, worldHeight);
        float[] m = {
                scales[0], 0f, 0f, 0f,
                0f, scales[1], 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        };

        return m;
    }

    public static float[] getTranslationMatrix(float x, float y) {
        /** Get a translation matrix for the given x and y translations. */
        float[] m = {
                1f, 0f, 0f, x,
                0f, 1f, 0f, y,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        };

        return m;
    }

    public static float[] getTranslateMatrix(float worldWidth, float worldHeight) {
        /** Get the translation matrix for the given world width and height. */
        float[] xy = getTranslation(worldWidth, worldHeight);
        return getTranslationMatrix(xy[0], xy[1]);
    }

    public static float[] matMult(float[] A, float[] B) {
        /** Return the 4x4 matrix multiplication result. */
        float[] C = {
                A[0] * B[0] + A[1] * B[4] + A[2] * B[8] + A[3] * B[12],
                A[0] * B[1] + A[1] * B[5] + A[2] * B[9] + A[3] * B[13],
                A[0] * B[2] + A[1] * B[6] + A[2] * B[10] + A[3] * B[14],
                A[0] * B[3] + A[1] * B[7] + A[2] * B[11] + A[3] * B[15],
                A[4] * B[0] + A[5] * B[4] + A[6] * B[8] + A[7] * B[12],
                A[4] * B[1] + A[5] * B[5] + A[6] * B[9] + A[7] * B[13],
                A[4] * B[2] + A[5] * B[6] + A[6] * B[10] + A[7] * B[14],
                A[4] * B[3] + A[5] * B[7] + A[6] * B[11] + A[7] * B[15],
                A[8] * B[0] + A[9] * B[4] + A[10] * B[8] + A[11] * B[12],
                A[8] * B[1] + A[9] * B[5] + A[10] * B[9] + A[11] * B[13],
                A[8] * B[2] + A[9] * B[6] + A[10] * B[10] + A[11] * B[14],
                A[8] * B[3] + A[9] * B[7] + A[10] * B[11] + A[11] * B[15],
                A[12] * B[0] + A[13] * B[4] + A[14] * B[8] + A[15] * B[12],
                A[12] * B[1] + A[13] * B[5] + A[14] * B[9] + A[15] * B[13],
                A[12] * B[2] + A[13] * B[6] + A[14] * B[10] + A[15] * B[14],
                A[12] * B[3] + A[13] * B[7] + A[14] * B[11] + A[15] * B[15]
        };

        return C;
    }

    public static float[] getTranslation(float worldWidth, float worldHeight) {
        return new float[] {-worldWidth / 2, -worldHeight / 2};
    }

    public static float[] getGLScale(float screenWidth, float screenHeight, float worldWidth, float worldHeight) {
        /** Get the x and y scale factors */
        float widthRatio = worldWidth / screenWidth;
        float heightRatio = worldHeight / screenHeight;
        float screenScale;
        float worldScale;
        float scaleX;
        float scaleY;
        if (widthRatio > heightRatio) {
            scaleX =  1f / worldWidth * 2;

            screenScale = screenHeight / screenWidth;
            worldScale = worldHeight / worldWidth;
            scaleY = worldScale / screenScale / worldHeight * 2;
        } else {
            screenScale = screenWidth / screenHeight;
            worldScale = worldWidth / worldHeight;
            scaleX = worldScale / screenScale / worldWidth * 2;


            scaleY = 1f / worldHeight * 2;
        }

        return new float[] {scaleX, scaleY};
    }

    public static float[] getScreenScale(float screenWidth, float screenHeight, float worldWidth, float worldHeight) {
        float widthRatio = worldWidth / screenWidth;
        float heightRatio = worldHeight / screenHeight;
        float screenScale;
        float worldScale;
        float scaleX;
        float scaleY;
        if (widthRatio > heightRatio) {
            scaleX =  screenWidth / worldWidth;

            screenScale = screenHeight / screenWidth;
            worldScale = worldHeight / worldWidth;
            scaleY = worldScale / screenScale / worldHeight * screenHeight;
        } else {
            screenScale = screenWidth / screenHeight;
            worldScale = worldWidth / worldHeight;
            scaleX = worldScale / screenScale / worldWidth  * screenWidth;

            scaleY = screenHeight / worldHeight;
        }

        return new float[] {scaleX, scaleY};
    }
}