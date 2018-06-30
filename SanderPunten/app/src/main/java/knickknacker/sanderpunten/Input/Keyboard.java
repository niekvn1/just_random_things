package knickknacker.sanderpunten.Input;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/** This file has functions to show and hide the SoftInputKeyboard. */

public abstract class Keyboard {
    public static void show(View view) {
        /** Show the keyboard. */
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void hide(View view) {
        /** Hide the keyboard. */
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
