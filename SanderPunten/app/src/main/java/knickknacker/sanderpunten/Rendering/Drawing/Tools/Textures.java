package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by Niek on 20-1-2018.
 *
 * Functions for using Textures with OpenGL ES 2.0.
 */

public abstract class Textures {
    public static final float[] rectangleCoords = {
    /** Return Texture coordinates for a rectangle. */
            0f, 1f,
            0f, 0f,
            1f, 1f,
            1f, 0f
    };

    public static int[] loadTexture(Context context, int[] ids) {
        /** Load the given textures from the Drawables resource. */
        int[] textures = new int[ids.length];
        GLES20.glGenTextures(ids.length, textures, 0);

        if (textures[0] == GLES20.GL_FALSE)
            throw new RuntimeException("Error loading texture");

        for (int i = 0; i < ids.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), ids[i], options);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, b, 0);
            b.recycle();
        }

        return textures;
    }

    public static int[] loadBitmapTexture(Bitmap bitmap) {
        /** Load bitmap as a texture. */
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST );
        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR );
        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE );

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        return textureIds;
    }
}
