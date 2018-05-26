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
import knickknacker.sanderpunten.Drawing.Properties.Colors;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutManager implements GLRenderCallback {
    private LayoutBox root = null;
    private GLRenderer renderer;
    private GLSurfaceView view;
    private Activity act;

    private int width = 0;
    private int height = 0;


    public LayoutManager (Activity act) {
        this.act = act;
    }

    public void surfaceCreatedCallback() {
        root = new LayoutBox(null);
        root.setBackgroundTexture(renderer.getTextures()[0]);
        layoutSetup();
    }

    public void surfaceChangedCallback(int width, int height) {
        this.width = width;
        this.height = height;

        if (root == null) {
            root = new LayoutBox(null, width, height);
        } else {
            root.newResolution(width, height);
        }

        renderer.layoutDrawables(root);
    }

    private void layoutSetup() {
        LayoutBox child = new LayoutBox(root, 0.1f, 0.9f, 0.1f, 0.9f);



//        ButtonMenu menu = new ButtonMenu(3, 0.1f, 0.1f, 0.1f, 0.1f, 0.2f);
//        ArrayList<float[]> button_points = menu.getButtonsPoints();
//
//        TriangleStrip strip;
//        for (float[] points : button_points) {
//            strip = new TriangleStrip(points, Colors.WHITE_TRANS, -1, null);
//            this.renderer.addRatioDrawable(strip);
//        }
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
}
