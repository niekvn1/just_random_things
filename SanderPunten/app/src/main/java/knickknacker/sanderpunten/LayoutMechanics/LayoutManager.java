package knickknacker.sanderpunten.LayoutMechanics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import knickknacker.sanderpunten.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Drawing.Drawables.Text;
import knickknacker.sanderpunten.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Drawing.Tools.GLRenderCallback;
import knickknacker.sanderpunten.Drawing.Tools.GLRenderer;
import knickknacker.sanderpunten.LayoutMechanics.Objects.DrawObjects;
import knickknacker.sanderpunten.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Drawing.Tools.TextManager;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutManager implements GLRenderCallback {
    private LayoutBox root = null;
    private GLRenderer renderer;
    private GLSurfaceView view;
    private Activity act;
    private TextManager textManager;
    private TouchListener touchListener;

    private boolean drawEdges = true;

    private int width = 0;
    private int height = 0;
    private int longSide = 0;
    private boolean initial = true;


    public LayoutManager (Activity act) {
        this.act = act;
    }

    public void surfaceCreatedCallback() {
        textManager = new TextManager(act.getAssets());

        root = new LayoutBox(null);
        root.setBackgroundTexture(renderer.getTextures()[0]);
        root.setColor(Colors.WHITE);
        ((LayoutManagerCallback) act).surfaceCreatedCallback(root, textManager);
    }

    public void surfaceChangedCallback(int width, int height) {
        this.width = width;
        this.height = height;
        longSide = width > height ? height : width;

        if (initial) {
            initial = false;
            textManager.load( "font/well_bred.otf", longSide / 30, 0.01f, 0.01f );
            layoutInit();
            layoutDrawables(root);
        } else {
            root.newResolution(width, height);
        }
    }

    private void layoutInit() {
        root.init(width, height);
        root.initChilderen();
    }

//    public void layoutBoxToDrawable(LayoutBox layoutBox) {
//        if (layoutBox instanceof TextBox) {
//            textBoxToDrawable((TextBox) layoutBox);
//        } else {
//            normalBoxToDrawable(layoutBox);
//        }
//
////        edgesToDrawables(layoutBox);
//    }

    public void layoutDrawables(LayoutBox layoutBox) {
        ArrayList<Drawable> drawables = layoutBox.toDrawable(drawEdges);
        for (Drawable d : drawables) {
            if (d != null) {
                renderer.addDrawable(d);
            }
        }


        for (LayoutBox child : layoutBox.getChilderen()) {
            layoutDrawables(child);
        }
    }

    private void textBoxToDrawable(TextBox textBox) {
        normalBoxToDrawable(textBox);
        for (Text text : textBox.getTexts()) {
            renderer.addDrawable(text);
        }
    }

    private void normalBoxToDrawable(LayoutBox layoutBox) {
        if (layoutBox.getBackgroundTexture() != -1 || layoutBox.getColor() != null) {
            TriangleStrip background = new TriangleStrip(DrawObjects.getBackgroundPoints(layoutBox.getCorners()),
                    layoutBox.getColor(), layoutBox.getBackgroundTexture(),
                    DrawObjects.get_background_texcoords());
            background.setTransformMatrix(layoutBox.getTransformMatrix());
            renderer.addDrawable(background);
        }
    }

    public void onCreate() {
        if (hasOpenGL2()) {
            renderer = new GLRenderer(act, this);

            view = new GLSurfaceView(act);
            view.setEGLContextClientVersion(2);
            view.setPreserveEGLContextOnPause(true);
            view.setRenderer(renderer);
            act.setContentView(view);

            touchListener = new TouchListener(this);
            view.setOnTouchListener(touchListener);
        } else {
            act.finish();
        }
    }

    private boolean hasOpenGL2() {
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public void onResume() {
        if (view != null) {
            view.onResume();
        }
    }

    public void onPause() {
        if (view != null) {
            view.onPause();
        }
    }

    public void onDestroy() {
        renderer.onDestroy();
    }

    public GLSurfaceView getView() {
        return view;
    }

    public LayoutBox getRoot() {
        return root;
    }
}
