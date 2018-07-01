package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import android.util.Log;
import android.view.KeyEvent;

import knickknacker.sanderpunten.Input.KeyboardCallback;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchListener.TouchData;

/**
 * Created by Niek on 3-6-2018.
 */

public class TextBar extends TextBox implements KeyboardCallback {
    private TextBarCallback callback;
    private boolean showing = false;

    public TextBar(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(manager, parent, left, right, bottom, top, relative);
    }

    @Override
    public void onTouchEvent(TouchData data) {
        if (!showing) {
            super.onTouchEvent(data);
        } else {
            cancel();
        }
    }

    @Override
    protected void onTouchUp(TouchData data) {
        Log.i("TextBar", "OnTouch");
        showKeyboard();
        super.onTouchUp(data);
    }

    private void showKeyboard() {
        showing = true;
        manager.setTouchListenerRoot(this);
        manager.getView().setKeyboardCallback(this);
        manager.getView().showKeyboard(true);
    }

    public void keyboardInput(int keyCode, KeyEvent event) {
        int uniChar = event.getUnicodeChar();
        String character = new String(new int[] {uniChar}, 0, 1);
        System.out.println("KEY: " + character + " " + keyCode);
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
//            Digits:
            addCharacter(event);
        } else if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
//            Letters:
            addCharacter(event);
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
//            Space:
            addCharacter(event);
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            Enter:
            done();
        } else if(keyCode == KeyEvent.KEYCODE_DEL) {
//            Backspace:
            if (text.length() > 0) {
                delete(text.length() - 1);
            }
        }
    }

    public void onToggle(int height, int oldHeight) {
        if (height != oldHeight) {
            float transY = bottom - 50 * manager.getUnit();
            if (transY > oldHeight - height) {
                manager.forceProjection(manager.getWidth(), oldHeight, 0, -(oldHeight - height));
            } else {
                manager.forceProjection(manager.getWidth(), oldHeight, 0, -transY);
            }
        } else {
            manager.stopForceProjection();
            manager.getView().setKeyboardCallback(null);
        }
    }

    private void addCharacter(KeyEvent event) {
        int uniChar = event.getUnicodeChar();
        String character = new String(new int[] {uniChar}, 0, 1);
        setText(text + character);
    }

    private void delete(int i) {
        String prefix = text.substring(0, i);
        String suffix = text.substring(i + 1, text.length());
        setText(prefix + suffix);
    }

    private void done() {
        cancel();
        callback.onTextCommitted(this);
        setText(text);
    }

    private void cancel() {
        manager.getView().showKeyboard(false);
        manager.setTouchListenerRoot(manager.getRoot());
        showing = false;
    }

    public void setCallback(TextBarCallback callback) {
        this.callback = callback;
    }
}
