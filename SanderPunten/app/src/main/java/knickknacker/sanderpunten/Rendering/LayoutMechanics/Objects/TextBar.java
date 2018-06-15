package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchListener.TouchData;

/**
 * Created by Niek on 3-6-2018.
 */

public class TextBar extends TextBox {
    public TextBar(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(manager, parent, left, right, bottom, top, relative);
    }

    @Override
    protected void onTouchUp(TouchData data) {
        System.out.println("Accessing Textbar");
        showKeyboard(true);
        super.onTouchUp(data);
    }

    private void showKeyboard(boolean show) {
        manager.getView().requestFocus();
//        manager.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) manager.getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            if (show) {
                imm.showSoftInput(manager.getView(), InputMethodManager.SHOW_FORCED);
            } else {
                imm.hideSoftInputFromWindow(manager.getView().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
