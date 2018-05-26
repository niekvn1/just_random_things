package knickknacker.sanderpunten.Drawing.Tools;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import knickknacker.sanderpunten.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Drawing.Drawables.Text;
import knickknacker.sanderpunten.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Drawing.GLRenderCallback;
import knickknacker.sanderpunten.Drawing.Objects.DrawObjects;
import knickknacker.sanderpunten.Drawing.Objects.LayoutBox;
import knickknacker.sanderpunten.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Drawing.TextManager;
import knickknacker.sanderpunten.R;

/**
 * Created by Niek on 22-5-2018.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private Activity act;
    private GLRenderCallback callback;
    private TextManager textManager;

    private int[] vertexHandles;
    private int[] fragmentHandles;
    private int[] programHandles;

    private int[] textures;

    private ArrayList<Drawable> drawables = new ArrayList<>();

    private float width, height;
    private boolean drawEdges = true;

    public GLRenderer(Activity act, GLRenderCallback callback) {
        this.act = act;
        this.callback = callback;
    }

    private void loadTextures() {
        int[] ids = {R.drawable.struissander};
        textures = Textures.loadTexture(act, ids);
    }

    private void setPrograms(ArrayList<Drawable> drawables) {
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
        textManager = new TextManager(act.getAssets());
        textManager.load( "font/well_bred.ttf", 35, 0.01f, 0.01f );

        loadTextures();
        callback.surfaceCreatedCallback();

        initShader();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width_, int height_) {
        callback.surfaceChangedCallback(width_, height_);
        Text text = textManager.getText( "Test Text", 0.0f, 0.0f, Colors.GREEN);
        drawables.add(text);

        width = width_;
        height = height_;

        setPrograms(drawables);

        setProjections();

        GLES20.glViewport(0, 0, width_, height_);
        GLES20.glClearColor(1f, 1f, 1f, 1f);
    }

    private void setProjections() {
        float[] scale = Matrices.getProjectionMatrix(width, height, width, height);
        float[] trans = Matrices.getTranslateMatrix(width, height);
        int mat;
        for (int i = 0; i < programHandles.length; i++) {
            GLES20.glUseProgram(programHandles[i]);
            mat = GLES20.glGetUniformLocation(programHandles[i], "u_Projection");
            GLES20.glUniformMatrix4fv(mat, 1, false, Shaders.getFloatBuffer(Matrices.matMult(scale, trans)));
        }
    }

    private void drawList(ArrayList<Drawable> drawables) {
        for (Drawable d : drawables) {
            d.draw();
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
        GLES20.glClear(clearMask);

        drawList(drawables);
    }

    public void addDrawable(Drawable d) {
        drawables.add(d);
    }

    public void layoutDrawables(LayoutBox root) {
        if (root.getBackgroundTexture() != - 1) {
            TriangleStrip background = new TriangleStrip(DrawObjects.getBackgroundPoints(root.getCorners()),
                    Colors.WHITE, root.getBackgroundTexture(),
                    DrawObjects.get_background_texcoords());
            background.setTransformMatrix(root.getTransformMatrix());
            addDrawable(background);
        }

        if (drawEdges) {
            float[][] edges = root.getEdges(5f);
            TriangleStrip edge_stip;
            for (int i = 0; i < 4; i++) {
                edge_stip = new TriangleStrip(edges[i], Colors.RED, -1, null);
                edge_stip.setTransformMatrix(root.getTransformMatrix());
                addDrawable(edge_stip);
            }
        }
    }

    public void onDestroy() {
        Shaders.clear(programHandles, vertexHandles,
                fragmentHandles);
        GLES20.glDeleteTextures(textures.length, textures, 0);
    }

    public int[] getTextures() {
        return textures;
    }

    private void printArray(float[] f, int n) {
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
