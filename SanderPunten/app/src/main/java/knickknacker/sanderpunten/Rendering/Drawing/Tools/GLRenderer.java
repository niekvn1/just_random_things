package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Text;
import knickknacker.sanderpunten.Rendering.Layout;

/**
 * Created by Niek on 22-5-2018.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private Activity act;
    private GLRenderCallback callback;

    private int[] vertexHandles;
    private int[] fragmentHandles;
    private int[] programHandles;

    private int[] textures;

    private Layout layout;

    private float width, height, worldWidth, worldHeight;
    private boolean initial = true;

    public GLRenderer(Activity act, GLRenderCallback callback) {
        this.act = act;
        this.callback = callback;
    }

    public void loadTextures(int[] ids) {
        textures = Textures.loadTexture(act, ids);
    }

    public void setPrograms(ArrayList<Drawable> drawables) {
        for (Drawable d : drawables) {
            if (d instanceof Text) {
                d.init();
                d.setProgramHandle(programHandles[2]);
            } else if (d.getTexture() == -1) {
                d.init();
                d.setProgramHandle(programHandles[0]);
            } else {
                d.init();
                d.setProgramHandle(programHandles[1]);
            }
        }
    }

    private void initShader() {
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
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        callback.surfaceCreatedCallback();

        initShader();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width_, int height_) {
        callback.surfaceChangedCallback(width_, height_);

        width = width_;
        height = height_;

        if (initial) {
            setPrograms(layout.getDrawables());
            layout.setDrawInitialized(true);
            initial = false;
        }

        setProjections();

        GLES20.glViewport(0, 0, width_, height_);
        GLES20.glClearColor(1f, 1f, 1f, 1f);

        System.out.println("Ready to draw: " + layout.getDrawables().size() + " layout.");
    }

    private void setProjections() {
        float[] scale = Matrices.getProjectionMatrix(width, height, worldWidth, worldHeight);
        float[] trans = Matrices.getTranslateMatrix(worldWidth, worldHeight);
        float[] proj = Matrices.matMult(scale, trans);
        int mat;
        for (int i = 0; i < programHandles.length; i++) {
            GLES20.glUseProgram(programHandles[i]);
            mat = GLES20.glGetUniformLocation(programHandles[i], "u_Projection");
            GLES20.glUniformMatrix4fv(mat, 1, false, Shaders.getFloatBuffer(proj));
        }
    }

    private void drawList(ArrayList<Drawable> drawables) {
        for (Drawable d : drawables) {
            d.checkForUpdates();
            d.draw();
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        callback.onDrawCallback();

        int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
        GLES20.glClear(clearMask);

        drawList(layout.getDrawables());
    }

    public void onDestroy() {
        Shaders.clear(programHandles, vertexHandles,
                fragmentHandles);
        GLES20.glDeleteTextures(textures.length, textures, 0);
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public int[] getTextures() {
        return textures;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public void setWorldWidth(float worldWidth) {
        this.worldWidth = worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(float worldHeight) {
        this.worldHeight = worldHeight;
    }

    public void printArray(float[] f, int n) {
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
