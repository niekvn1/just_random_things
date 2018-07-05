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

    public TextBar(LayoutManager manager, LayoutBox parent) {
        super(manager, parent);
    }

    public TextBar(LayoutManager manager, LayoutBox parent, float width, float height, boolean relative) {
        this(manager, parent, 0f, width , 0f, height, relative);
    }

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
        Log.i("KEY", character + " " + keyCode);
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
//            Digits:
            addCharacter(event);
        } else if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_PERIOD) {
//            Letters:
            addCharacter(event);
        } else if (keyCode >= KeyEvent.KEYCODE_MINUS && keyCode <= KeyEvent.KEYCODE_SLASH) {
//            Math operators:
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

    public void onToggle(float width, float defaultWidth, float height, float defaultHeight) {
        float transY = bottom - 50 * manager.getUnit();
        if (transY > defaultHeight - height) {
            manager.forceProjection(manager.getWorldWidth() * (width / defaultWidth), manager.getWorldHeight() * (height / defaultHeight), 0, -(defaultHeight - height));
        } else {
            manager.forceProjection(manager.getWorldWidth() * (width / defaultWidth), manager.getWorldHeight() * (height / defaultHeight), 0, -transY);
        }
    }

    private void addCharacter(KeyEvent event) {
        int uniChar = event.getUnicodeChar();
        String character = new String(new int[] {uniChar}, 0, 1);
        if (text == null) {
            setText(character);
        } else {
            setText(text + character);
        }
    }

    private void delete(int i) {
        String prefix = text.substring(0, i);
        String suffix = text.substring(i + 1, text.length());
        setText(prefix + suffix);
    }

    private void done() {
        cancel();
        if (callback != null) {
            callback.onTextCommitted(this);
        }

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
