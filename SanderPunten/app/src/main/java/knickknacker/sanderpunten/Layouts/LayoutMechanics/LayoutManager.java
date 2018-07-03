package knickknacker.sanderpunten.Layouts.LayoutMechanics;

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
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Utilities.Flag;
import knickknacker.tcp.Networking.ConcurrentList;

/**
 * Created by Niek on 26-5-2018.
 *
 * Manages drawing of different layouts.
 */

public class LayoutManager implements GLRenderCallback {
    public final static int INTERRUPT_CHANGE = 1;
    public final static int INTERRUPT_LAYOUT_LOADED = 2;
    public final static int INTERRUPT_LAYOUT_UNLOAD = 3;
    public final static int INTERRUPT_NEW_PROJECTION = 4;
    public final static int INTERRUPT_FONT_LOADER = 5;
    public final static int INTERRUPT_LAYOUT_RELOAD = 6;

    private Layout[] layouts;
    private ConcurrentList<Layout> needLoad;
    private ConcurrentList<Layout> needUnload;
    private ConcurrentList<Layout> needReload;
    private ConcurrentList<TextManager> fontLoad;
    private ArrayList<LayoutBox> directAccess;
    private GLRenderer renderer;
    private DrawView view;
    private Activity act;
    private TouchListener touchListener;

    private boolean drawEdges = false;

    private int width, height, worldWidth, worldHeight;
    private float transX, transY;
    private boolean translate = false;
    private boolean resolutionSet = false;

    private float unit = 0;
    private int layoutCount;
    private int using = 0;

    private Flag interrupt = new Flag();


    public LayoutManager (Activity act, int layoutCount) {
        this.act = act;
        this.layoutCount = layoutCount < 1 ? 1 : layoutCount;
        layouts = new Layout[this.layoutCount];
        directAccess = new ArrayList<>();
        needLoad = new ConcurrentList<>();
        needUnload = new ConcurrentList<>();
        fontLoad = new ConcurrentList<>();
        needReload = new ConcurrentList<>();
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
        ((LayoutManagerCallback) act).setupLayout(layouts[using]);
    }

    public void surfaceChangedCallback(int width, int height) {
        /** Initialize the layout or update it on the new resolution. */
        Log.i("NEW RESOLUTION", width + " " + height);
        this.width = worldWidth = width;
        this.height = worldHeight = height;

        Layout layout = layouts[using];
        if (!resolutionSet) {
            resolutionSet = true;
            unit = width > height ? (float) height / 1000 : (float) width / 1000;
            loadFonts();
            layout.init(width, height);
            renderer.setDrawables(layout.getDrawables());
            renderer.initDrawables(layout.getDrawables());
            layout.setDrawInitialized(true);
            layout.setUsed(true);
        } else {
            for (Layout l : layouts) {
                if (l != null && l.isUsed()) {
                    l.init(width, height);
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

    public void forceProjection(int viewWidth, int viewHeight, float x, float y) {
        Log.i("FORCE PROJECTION", "width: " + viewWidth + " height: " + viewHeight + " x: " + x + " y: " + y);
        transX = x;
        transY = y;
        worldWidth = viewWidth;
        worldHeight = viewHeight;
        translate = true;
        interrupt.set(INTERRUPT_NEW_PROJECTION);
    }

    public void stopForceProjection() {
        translate = false;
        interrupt.set(INTERRUPT_NEW_PROJECTION);
    }

    public void onDrawCallback() {
        /** Check if an interrupt has occurred. */
        if (interrupt.isSet()) {
            handleInterrupt();
        }
    }

    private void handleInterrupt() {
        /** Handle different interrupt types. */
        if (interrupt.hasSet(INTERRUPT_FONT_LOADER)) {
            loadFonts();
        }

        if (interrupt.hasSet(INTERRUPT_CHANGE)) {
            change();
        }

        if (interrupt.hasSet(INTERRUPT_LAYOUT_LOADED)) {
            layoutLoaded();
        }

        if (interrupt.hasSet(INTERRUPT_LAYOUT_UNLOAD)) {
            layoutUnload();
        }

        if (interrupt.hasSet(INTERRUPT_NEW_PROJECTION)) {
            newProjection();
        }

        if (interrupt.hasSet(INTERRUPT_LAYOUT_RELOAD)) {
            layoutReload();
        }
    }

    private void loadFonts() {
        for (TextManager font : fontLoad.getCopy()) {
            font.load(unit);
            Log.i("FONT LOADER", "" + font.getSize());
        }

        fontLoad.clear();
        interrupt.unset(INTERRUPT_FONT_LOADER);
    }

    private void change() {
        Layout layout = layouts[using];
        layout.init(width, height);
        Log.i("MANAGER", "CHANGE");
        renderer.setDrawables(layout.getDrawables());
        if (!layout.isDrawInitialized()) {
            renderer.initDrawables(layout.getDrawables());
            layout.setDrawInitialized(true);
        }

        interrupt.unset(INTERRUPT_CHANGE);
    }

    private void layoutLoaded() {
        for (Layout l : needLoad.getCopy()) {
            l.init(width, height);

            if (!l.isDrawInitialized()) {
                for (Drawable d : l.getDrawables()) {
                    renderer.newDrawable(d);
                    renderer.initDrawable(d);
                }
            }
        }

        needLoad.clear();
        interrupt.unset(INTERRUPT_LAYOUT_LOADED);
    }

    private void layoutUnload() {
        for (Layout l : needUnload.getCopy()) {
            for (Drawable d : l.getDrawables()) {
                renderer.removeDrawable(d);
            }
        }

        interrupt.unset(INTERRUPT_LAYOUT_UNLOAD);
    }

    private void layoutReload() {
        for (Layout l : needReload.getCopy()) {
            ArrayList<Drawable> newDrawables = l.init(width, height, false);
            for (Drawable d : newDrawables) {
                renderer.newDrawable(d);
                renderer.initDrawable(d);
            }
        }

        needReload.clear();
        interrupt.unset(INTERRUPT_LAYOUT_RELOAD);
    }

    private void newProjection() {
        for (Layout l : layouts) {
            if (l != null && l.isUsed()) {
                l.init(worldWidth, worldHeight);
            }
        }

        setProjection();
        interrupt.unset(INTERRUPT_NEW_PROJECTION);
    }

    private boolean hasOpenGL2() {
        /** Check whether the device has OpenGL ES 2.0. */
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public Layout newLayout(int index) {
        /** Return a new Layout instance, that will also be save on the given index in this
         * manager. */
        if (index < layoutCount) {
            layouts[index] = new Layout(this, drawEdges);
            return layouts[index];
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
        touchListener.setRoot(layout.getRoot());
        layouts[using].setUsed(false);
        using = index;
        layout.setUsed(true);
        interrupt.set(INTERRUPT_CHANGE);
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
            needLoad.add(layout);
            interrupt.set(INTERRUPT_LAYOUT_LOADED);
        }
    }

    public void unload(int index) {
        /** Unload a loaded layout. */
        touchListener.setRoot(layouts[using].getRoot());
        layouts[index].setUsed(false);
        needUnload.add(layouts[index]);
        interrupt.set(INTERRUPT_LAYOUT_UNLOAD);
    }

    public void reload(Layout layout) {
        needReload.add(layout);
        interrupt.set(INTERRUPT_LAYOUT_RELOAD);
    }

    public void loadFont(TextManager font) {
        fontLoad.add(font);
        interrupt.set(INTERRUPT_FONT_LOADER);
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

    public void setTouchListenerRoot(LayoutBox box) {
        touchListener.setRoot(box);
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
