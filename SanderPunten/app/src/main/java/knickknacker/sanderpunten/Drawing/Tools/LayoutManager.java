package knickknacker.sanderpunten.Drawing.Tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import knickknacker.sanderpunten.Drawing.Drawables.TriangleStrip;
import knickknacker.sanderpunten.Drawing.GLRenderCallback;
import knickknacker.sanderpunten.Drawing.Objects.ButtonMenu;
import knickknacker.sanderpunten.Drawing.Objects.DrawObjects;
import knickknacker.sanderpunten.Drawing.Objects.LayoutBox;
import knickknacker.sanderpunten.Drawing.Objects.RelativeLayoutBox;
import knickknacker.sanderpunten.Drawing.Properties.Colors;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutManager implements GLRenderCallback {
    private LayoutBox root = null;
    private GLRenderer renderer;
    private GLSurfaceView view;
    private Activity act;

    private boolean drawEdges = true;

    private int width = 0;
    private int height = 0;


    public LayoutManager (Activity act) {
        this.act = act;
    }

    public void surfaceCreatedCallback() {
        root = new LayoutBox(null);
        root.setBackgroundTexture(renderer.getTextures()[0]);
    }

    public void surfaceChangedCallback(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;

            if (root == null) {
                root = new LayoutBox(null, width, height);
            } else {
                root.newResolution(width, height);
            }

            layoutSetup();
            layoutDrawables(root);
        }
    }

    private void layoutSetup() {
        LayoutBox child = new RelativeLayoutBox(root, 0.1f, 0.9f, 0.1f, 0.9f);



//        ButtonMenu menu = new ButtonMenu(3, 0.1f, 0.1f, 0.1f, 0.1f, 0.2f);
//        ArrayList<float[]> button_points = menu.getButtonsPoints();
//
//        TriangleStrip strip;
//        for (float[] points : button_points) {
//            strip = new TriangleStrip(points, Colors.WHITE_TRANS, -1, null);
//            this.renderer.addRatioDrawable(strip);
//        }
    }

    public void layoutBoxToDrawables(LayoutBox layoutBox) {
        if (layoutBox.getBackgroundTexture() != -1) {
            TriangleStrip background = new TriangleStrip(DrawObjects.getBackgroundPoints(layoutBox.getCorners()),
                    Colors.WHITE, layoutBox.getBackgroundTexture(),
                    DrawObjects.get_background_texcoords());
            background.setTransformMatrix(layoutBox.getTransformMatrix());
            renderer.addDrawable(background);
        }

        edgesToDrawables(layoutBox);
    }

    public void layoutDrawables(LayoutBox layoutBox) {
        layoutBoxToDrawables(layoutBox);
        for (LayoutBox child : layoutBox.getChilderen()) {
            layoutDrawables(child);
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

    public void edgesToDrawables(LayoutBox layoutBox) {
        if (drawEdges) {
            float[][] edges = layoutBox.getEdges(5f);
            TriangleStrip edge_stip;
            for (int i = 0; i < 4; i++) {
                edge_stip = new TriangleStrip(edges[i], Colors.RED, -1, null);
                edge_stip.setTransformMatrix(layoutBox.getTransformMatrix());
                renderer.addDrawable(edge_stip);
            }
        }
    }
}
