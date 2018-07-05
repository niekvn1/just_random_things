package knickknacker.sanderpunten.Input;

import android.view.KeyEvent;

/** Functions that a KeyboardCallback should have. */

public interface KeyboardCallback {

    /** Receives the keycode and KeyEvent in the KeyBoard callback */
    void keyboardInput(int keyCode, KeyEvent event);

    /** Receives the height after the keyboard change in here. */
    void onToggle(float width, float defaultWidth, float height, float defaultHeight);
}
