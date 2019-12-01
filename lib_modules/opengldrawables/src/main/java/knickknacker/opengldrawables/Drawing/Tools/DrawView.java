package knickknacker.opengldrawables.Drawing.Tools;

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;

import knickknacker.opengldrawables.Drawing.Input.Keyboard;
import knickknacker.opengldrawables.Drawing.Input.KeyboardCallback;


/**
 * Created by Niek on 3-6-2018.
 *
 * A GLSurfaceView extended with functions to handle the SoftInputKeyboard input.
 */

public class DrawView extends GLSurfaceView {
    private SpannableStringBuilder text;
    private KeyboardCallback callback;
    private int portraitHeight = -1;
    private int portraitWidth = -1;
    private int landscapeHeight = -1;
    private int landscapeWidth = -1;
    private Activity act;

    public DrawView(Activity act) {
        super(act);
        this.act= act;
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        text = new SpannableStringBuilder();
    }

    public void adjust() {
        if (act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (portraitHeight == -1 && portraitWidth == -1) {
                portraitWidth = this.getWidth();
                portraitHeight = this.getHeight();
            }
        } else {
            if (landscapeHeight == -1 && landscapeWidth == -1) {
                landscapeWidth = this.getWidth();
                landscapeHeight = this.getHeight();
            }
        }

        Log.i("Adjust", "width: " + this.getWidth() + " height: " + this.getHeight());
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

    public KeyboardCallback getKeyboardCallback() {
        return callback;
    }

    public int getPortraitHeight() {
        return portraitHeight;
    }

    public int getPortraitWidth() {
        return portraitWidth;
    }

    public int getLandscapeHeight() {
        return landscapeHeight;
    }

    public int getLandscapeWidth() {
        return landscapeWidth;
    }
}
