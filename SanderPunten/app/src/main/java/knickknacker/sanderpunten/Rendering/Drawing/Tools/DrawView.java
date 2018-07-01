package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;

import knickknacker.sanderpunten.Input.Keyboard;
import knickknacker.sanderpunten.Input.KeyboardCallback;


/**
 * Created by Niek on 3-6-2018.
 *
 * A GLSurfaceView extended with functions to handle the SoftInputKeyboard input.
 */

public class DrawView extends GLSurfaceView {
    private SpannableStringBuilder text;
    private KeyboardCallback callback;
    private int defaultHeight = -1;
    private int defaultWidth = -1;
    private int width = -1;
    private int height = -1;

    public DrawView(Context context) {
        super(context);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        text = new SpannableStringBuilder();

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adjust();
            }
        });
    }

    private void adjust() {
        if (defaultHeight == -1 && defaultWidth == -1) {
            defaultWidth = this.getWidth();
            defaultHeight = this.getHeight();
        } else {
            width = this.getWidth();
            height = this.getHeight();
        }

//        int keyboardHeight = defaultHeight - height;
        if (callback != null) {
            callback.onToggle(height, defaultHeight);
        }
    }

    public void showKeyboard(boolean show) {
        if (show) {
            requestFocus();
            Keyboard.show(this);
        } else {
            Keyboard.hide(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /** When a key is pressed down, do ... */
        boolean code = super.onKeyDown(keyCode, event);
        return code;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /** When a key is released, send the keycode and event to the KeyboardCallback. */
        if (callback != null) {
            callback.keyboardInput(keyCode, event);
        }

        boolean code = super.onKeyUp(keyCode, event);
        return code;
    }

    public SpannableStringBuilder getText() {
        /** Return SpannableStringBuilder. */
        return text;
    }

    public void setText(SpannableStringBuilder text) {
        /** Set the SpannableStringBuilder. */
        this.text = text;
    }

    public void setKeyboardCallback(KeyboardCallback callback) {
        /** Set the KeyboardCallback. */
        this.callback = callback;
    }
}
