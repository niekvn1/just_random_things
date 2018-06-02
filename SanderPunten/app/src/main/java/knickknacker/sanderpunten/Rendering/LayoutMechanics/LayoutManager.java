package knickknacker.sanderpunten.Rendering.LayoutMechanics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.GLRenderCallback;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.GLRenderer;
import knickknacker.sanderpunten.Rendering.Layout;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener;

/**
 * Created by Niek on 26-5-2018.
 */

public class LayoutManager implements GLRenderCallback {
    private ArrayList<Layout> layouts = null;
    private GLRenderer renderer;
    private GLSurfaceView view;
    private Activity act;
    private TouchListener touchListener;

    private boolean drawEdges = true;

    private int width = 0;
    private int height = 0;
    private boolean initial = true;

    private float unit = 0;
    private int layoutCount;
    private int using = 0;
    private int layoutsSet = 0;


    public LayoutManager (Activity act, int layoutCount) {
        this.act = act;
        this.layoutCount = layoutCount < 1 ? 1 : layoutCount;
        layouts = new ArrayList<>();
    }

    public void onCreate() {
        if (hasOpenGL2()) {
            renderer = new GLRenderer(act, this);

            view = new GLSurfaceView(act);
            view.setEGLContextClientVersion(2);
            view.setPreserveEGLContextOnPause(true);
            view.setRenderer(renderer);
        } else {
            act.finish();
        }
    }

    public void surfaceCreatedCallback() {
        newLayout();
        touchListener = new TouchListener(this);
        view.setOnTouchListener(touchListener);
        ((LayoutManagerCallback) act).surfaceCreatedCallback(layouts.get(using).getRoot());
    }

    public void surfaceChangedCallback(int width, int height) {
        System.out.println("New Res: " + width + " " + height);
        this.width = width;
        this.height = height;
        renderer.setWorldWidth(this.width);
        renderer.setWorldHeight(this.height);

        if (initial) {
            unit = width > height ? (float) height / 1000 : (float) width / 1000;
            initial = false;
            layoutInit(using);
            layoutDrawables(using, layouts.get(using).getRoot());
            renderer.setLayout(layouts.get(using));
        } else {
            layouts.get(using).getRoot().newResolution(this.width, this.height);
        }
    }

    private void layoutInit(int index) {
        layouts.get(index).getRoot().init(width, height);
        layouts.get(index).getRoot().initChilderen();
    }

    public void layoutDrawables(int index, LayoutBox layoutBox) {
        ArrayList<Drawable> drawables = layoutBox.toDrawable(drawEdges);
        for (Drawable d : drawables) {
            if (d != null) {
                layouts.get(index).getDrawables().add(d);
            }
        }

        for (LayoutBox child : layoutBox.getChilderen()) {
            layoutDrawables(index, child);
        }
    }

    private boolean hasOpenGL2() {
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public LayoutBox newLayout() {
        if (layoutsSet < layoutCount) {
            layouts.add(new Layout(this));
            layoutsSet++;
            return layouts.get(layoutsSet - 1).getRoot();
        }

        return null;
    }

    public void switchLayout(int index) {
        layoutInit(index);
        layoutDrawables(index, layouts.get(index).getRoot());
        using = index;
        renderer.newLayout(layouts.get(using));
        renderer.interrupt(GLRenderer.INTERRUPT_CHANGE);
    }

    public void loadTextures(int[] ids) {
        renderer.loadTextures(ids);
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
        return layouts.get(using).getRoot();
    }

    public GLRenderer getRenderer() {
        return renderer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getUnit() {
        return unit;
    }

    public int[] getTextures() {
        return renderer.getTextures();
    }
}
