package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Text;

/**
 * Created by Niek on 22-5-2018.
 *
 * This class uses OpenGL ES 2.0 to draw Drawable.class. Drawables can be added to the list, they
 * should be initialized and their programs should be set, before they are added. When this is done
 * right they should be drawn on the screen without errors.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private Activity act;
    private GLRenderCallback callback;

    private int[] vertexHandles;
    private int[] fragmentHandles;
    private int[] programHandles;

    private int[] textures;

    private ArrayList<Drawable> drawables;

    private float width, height;

    public GLRenderer(Activity act, GLRenderCallback callback) {
        this.act = act;
        this.callback = callback;
        this.drawables = new ArrayList<>();
    }

    public void loadTextures(int[] ids) {
        /** Load textures from the Drawable Resource. */
        textures = Textures.loadTexture(act, ids);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        /** Surface is created, tell it to the callback. Initialize the shader programs. Set
         * the needed OpenGL flags. */
        callback.surfaceCreatedCallback();

        initShader();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1f, 1f, 1f, 1f);
    }

    private void initShader() {
        /** Create the needed OpenGL program, vertex and fragment handles and load the needed
         * shader programs to the GPU. */
        Shaders.clear(programHandles, vertexHandles,
                fragmentHandles);

        vertexHandles = new int[3];
        fragmentHandles = new int[3];
        programHandles = new int[3];

        vertexHandles[0] = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.DEFAULT_VERTEX_SHADER);
        fragmentHandles[0] = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.DEFAULT_FRAGMENT_SHADER);
        programHandles[0] = Shaders.loadProgram(vertexHandles[0], fragmentHandles[0]);

        vertexHandles[1] = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.TEXTURE_VERTEX_SHADER);
        fragmentHandles[1] = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.TEXTURE_FRAGMENT_SHADER);
        programHandles[1] = Shaders.loadProgram(vertexHandles[1], fragmentHandles[1]);

        vertexHandles[2] = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.TEXTURE_VERTEX_SHADER);
        fragmentHandles[2] = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.FONT_FRAGMENT_SHADER);
        programHandles[2] = Shaders.loadProgram(vertexHandles[2], fragmentHandles[2]);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width_, int height_) {
        /** Surface has changed, tell it to the callback. Adjust the projection matrix to the
         * new resolution. */
        width = width_;
        height = height_;

        callback.surfaceChangedCallback(width_, height_);

        GLES20.glViewport(0, 0, width_, height_);
        Log.i("GLRenderer","Drawables: " + drawables.size());
    }

    public void newDrawable(Drawable d) {
        /** Adds a new drawable, assumes that it is already initialized. */
        drawables.add(d);
    }

    public void initDrawables(ArrayList<Drawable> drawables) {
        /** Initializes all the given drawables. */
        for (Drawable d : drawables) {
            initDrawable(d);
        }
    }

    public void initDrawable(Drawable d) {
        /** Initialize the drawable by calling its init function and setting the shader program. */
        d.init();
        setProgram(d);
    }

    private void setProgram(Drawable d) {
        /** Set the program for the given drawable. The program is chosen by checking whether the
         * drawable uses a texture, whether the drawable draws text or if it only draws something
         * with a color. */
        if (d instanceof Text) {
            d.setProgramHandle(programHandles[2]);
        } else if (d.getTexture() == -1) {
            d.setProgramHandle(programHandles[0]);
        } else {
            d.setProgramHandle(programHandles[1]);
        }
    }

    public void setProjection(float[] proj) {
        /** Calculate the projection matrix and load it to the shader programs on the GPU. */
        int mat;
        for (int i = 0; i < programHandles.length; i++) {
            GLES20.glUseProgram(programHandles[i]);
            mat = GLES20.glGetUniformLocation(programHandles[i], "u_Projection");
            GLES20.glUniformMatrix4fv(mat, 1, false, Shaders.getFloatBuffer(proj));
        }
    }

    private void drawList(ArrayList<Drawable> drawables) {
        /** For a the drawables that should be drawn, check if it contains an update and draw it. */
        for (Drawable d : drawables) {
            d.checkForUpdates();
            d.draw();
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        /** For every frame, call the callback, draw all the drawables in the list. */
        callback.onDrawCallback();

        int clearMask = GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT;
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(clearMask);

        drawList(drawables);
    }

    public void onDestroy() {
        /** Destroy the resources. */
        Shaders.clear(programHandles, vertexHandles,
                fragmentHandles);
        GLES20.glDeleteTextures(textures.length, textures, 0);
    }

    public void destroy(ArrayList<Drawable> drawables) {
        /** Tell these drawables to destroy there resources (VBOs) */
        for (Drawable d : drawables) {
            d.destroy();
        }
    }

    public ArrayList<Drawable> getDrawables() {
        /** Get the drawables. */
        return drawables;
    }

    public void setDrawables(ArrayList<Drawable> drawables) {
        /** Set the drawables, assumes they are initialized. */
        this.drawables = drawables;
    }

    public void removeDrawable(Drawable d) {
        /** Remove the drawable. */
        this.drawables.remove(d);
    }

    public int[] getTextures() {
        /** Get the loaded textures. */
        return textures;
    }

    public void printArray(float[] f, int n) {
        /** Fancy array print function for testing. */
        System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
        for (int i = 0; i < f.length; i += n) {
            for (int j = 0; j < n; j++) {
                System.out.print(f[i + j] + " | ");
            }

            System.out.println();
        }
        System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
    }
}
