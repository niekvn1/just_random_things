package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import knickknacker.sanderpunten.Input.Keyboard;
import knickknacker.sanderpunten.Input.KeyboardCallback;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener.TouchData;

/**
 * Created by Niek on 3-6-2018.
 */

public class TextBar extends TextBox implements KeyboardCallback {
    TextBarCallback callback;

    public TextBar(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(manager, parent, left, right, bottom, top, relative);
    }

    @Override
    protected void onTouchUp(TouchData data) {
        showKeyboard();
        super.onTouchUp(data);
    }

    private void showKeyboard() {
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

    public void newHeight(int height) {
        manager.translateAll(0, height);
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
        manager.getView().setKeyboardCallback(null);
        manager.getView().showKeyboard(false);
        callback.onTextCommitted(this);
        setText(text);

    }

    public void setCallback(TextBarCallback callback) {
        this.callback = callback;
    }
}
