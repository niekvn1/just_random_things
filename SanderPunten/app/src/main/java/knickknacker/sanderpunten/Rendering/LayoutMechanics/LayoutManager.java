package knickknacker.sanderpunten.Rendering.LayoutMechanics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.util.Log;

import java.util.ArrayList;

import knickknacker.sanderpunten.Input.Keyboard;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.DrawView;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.GLRenderCallback;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.GLRenderer;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.Matrices;
import knickknacker.sanderpunten.Rendering.Layout;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener;

/**
 * Created by Niek on 26-5-2018.
 *
 * Manages drawing of different layouts.
 */

public class LayoutManager implements GLRenderCallback {
    public final static int INTERRUPT_CHANGE = 1;
    public final static int INTERRUPT_LAYOUT_LOADED = 2;
    public final static int INTERRUPT_LAYOUT_UNLOAD = 3;

    private Layout[] layouts;
    private ArrayList<Layout> needLoad;
    private ArrayList<Layout> needUnload;
    private ArrayList<LayoutBox> directAccess;
    private GLRenderer renderer;
    private DrawView view;
    private Activity act;
    private TouchListener touchListener;

    private boolean drawEdges = false;

    private int width, height;
    private float transX, transY;
    private boolean translate = false;
    private boolean resolutionSet = false;

    private float unit = 0;
    private int layoutCount;
    private int using = 0;
    private int loaded = -1;

    private int interrupt = 0;


    public LayoutManager (Activity act, int layoutCount) {
        this.act = act;
        this.layoutCount = layoutCount < 1 ? 1 : layoutCount;
        layouts = new Layout[this.layoutCount];
        directAccess = new ArrayList<>();
        needLoad = new ArrayList<>();
        needUnload = new ArrayList<>();
    }

    public void onCreate() {
        /** If OpenGL ES 2.0 is available, do set up the view and renderer. */
        if (hasOpenGL2()) {
            renderer = new GLRenderer(act, this);

            view = new DrawView(act);
            view.setEGLContextClientVersion(2);
            view.setPreserveEGLContextOnPause(true);
            view.setRenderer(renderer);
        } else {
            act.finish();
        }
    }

    public void surfaceCreatedCallback() {
        /** Create an initial layout with TouchListener and send it to the callback. */
        newLayout(0);
        touchListener = new TouchListener(this);
        view.setOnTouchListener(touchListener);
        ((LayoutManagerCallback) act).setupLayout(layouts[using].getRoot());
    }

    public void surfaceChangedCallback(int width, int height) {
        /** Initialize the layout or update it on the new resolution. */
        Log.i("NEW RESOLUTION", width + " " + height);
        this.width = width;
        this.height = height;

        Layout layout = layouts[using];
        if (!resolutionSet) {
            resolutionSet = true;
            unit = width > height ? (float) height / 1000 : (float) width / 1000;
            ((LayoutManagerCallback) act).loadLayout(unit);
            layout.init(width, height);
            renderer.setDrawables(layout.getDrawables());
            renderer.initDrawables(layout.getDrawables());
            layout.setDrawInitialized(true);
            layout.setUsed(true);
        } else {
            for (Layout l : layouts) {
                if (l != null && l.isUsed()) {
                    l.getRoot().newResolution(this.width, this.height);
                }
            }
        }

        setProjection();
    }

    private void setProjection() {
        float[] scale = Matrices.getProjectionMatrix(width, height, width, height);
        float[] trans = Matrices.getTranslateMatrix(width, height);
        if (translate) {
            float[] transAll = Matrices.getTranslationMatrix(transX, transY);
            trans = Matrices.matMult(transAll, trans);
        }

        float[] proj = Matrices.matMult(scale, trans);
        renderer.setProjection(proj);
    }

    public void translateAll(float x, float y) {
        transX = x;
        transY = y;
        translate = true;
    }

    public void disableTranslateAll() {
        translate = false;
    }

    public void onDrawCallback() {
        /** Check if an interrupt has occurred. */
        if (interrupt != 0) {
            handleInterrupt();
        }
    }

    private void handleInterrupt() {
        /** Handle different interrupt types. */
        Layout layout;
        switch (interrupt) {
            case INTERRUPT_CHANGE:
                layout = layouts[using];
                renderer.setDrawables(layout.getDrawables());
                if (!layout.isDrawInitialized()) {
                    renderer.initDrawables(layout.getDrawables());
                    layout.setDrawInitialized(true);
                }

                break;
            case INTERRUPT_LAYOUT_LOADED:
                for (Layout l : needLoad) {
                    if (!l.isInitialized()) {
                        l.init(width, height);
                    }

                    if (!l.isDrawInitialized()) {
                        for (Drawable d : l.getDrawables()) {
                            renderer.newDrawable(d);
                            renderer.initDrawable(d);
                        }
                    }
                }

                needLoad.clear();
                break;
            case INTERRUPT_LAYOUT_UNLOAD:
                for (Layout l : needUnload) {
                    for (Drawable d : l.getDrawables()) {
                        renderer.removeDrawable(d);
                    }
                }

                break;
        }

        interrupt = 0;
    }

    private boolean hasOpenGL2() {
        /** Check whether the device has OpenGL ES 2.0. */
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public LayoutBox newLayout(int index) {
        /** Return a new Layout instance, that will also be save on the given index in this
         * manager. */
        if (index < layoutCount) {
            layouts[index] = new Layout(this, drawEdges);
            return layouts[index].getRoot();
        }

        return null;
    }

    public LayoutBox getLayout(int index) {
        /** Return the root of the layout on the given index. */
        return layouts[index].getRoot();
    }

    public void switchLayout(int index) {
        /** Switch to the layout save on the given index in this manager. */
        Layout layout = layouts[index];
        if (!layout.isInitialized()) {
            layout.init(width, height);
        } else {
            layout.getRoot().newResolution(width, height);
        }

        touchListener.setRoot(layout.getRoot());
        layouts[using].setUsed(false);
        using = index;
        layout.setUsed(true);
        interrupt = INTERRUPT_CHANGE;
    }

    public void load(int index, boolean touch) {
        /** Load the given layout, without unloading the layout that is being used. If 'touch'
         * is true, the TouchListener will be set to the loaded layout. */
        Layout layout = layouts[index];
        if (touch) {
            touchListener.setRoot(layout.getRoot());
        }

        if (!layout.isUsed()) {
            layout.setUsed(true);
            interrupt = INTERRUPT_LAYOUT_LOADED;
            needLoad.add(layout);
        }
    }

    public void unload(int index) {
        /** Unload a loaded layout. */
        touchListener.setRoot(layouts[using].getRoot());
        layouts[index].setUsed(false);
        needUnload.add(layouts[index]);
        interrupt = INTERRUPT_LAYOUT_UNLOAD;
    }

    public void loadTextures(int[] ids) {
        /** Load the given Drawable resources as textures. */
        renderer.loadTextures(ids);
    }

    public void onResume() {
        /** On resume. */
        if (view != null) {
            view.onResume();
        }
    }

    public void onPause() {
        /** On pause, pause the view and hide the keyboard. */
        if (view != null) {
            view.onPause();
            Keyboard.hide(view);
        }
    }

    public void onDestroy() {
        /** On destroy, destroy the renderer and hide the keyboard. */
        renderer.onDestroy();
        Keyboard.hide(view);
    }

    public void addDirectAccess(LayoutBox box, String id) {
        /** Add direct access to a LayoutBox with a String as key. */
        box.setId(id);
        directAccess.add(box);
    }

    public LayoutBox getDirectAccess(String id) {
        /** Get a LayoutBox with direct access. */
        for (LayoutBox box : directAccess) {
            if (box.getId().equals(id)) {
                return box;
            }
        }

        return null;
    }

    public DrawView getView() {
        /** Get the view. */
        return view;
    }

    public LayoutBox getRoot() {
        /** Get the root of the used layout. */
        return layouts[using].getRoot();
    }

    public GLRenderer getRenderer() {
        /** Get the renderer. */
        return renderer;
    }

    public int getWidth() {
        /** Get the screen width. */
        return width;
    }

    public int getHeight() {
        /** Get the screen height. */
        return height;
    }

    public float getUnit() {
        /** Get the unit. */
        return unit;
    }

    public int[] getTextures() {
        /** Get the loaded textures from the renderer. */
        return renderer.getTextures();
    }

    public Activity getActivity() {
        /** Get the activity that uses the renderer. */
        return act;
    }
}
