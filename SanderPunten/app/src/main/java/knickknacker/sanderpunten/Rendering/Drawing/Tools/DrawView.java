package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.content.Context;
import android.opengl.GLSurfaceView;


/**
 * Created by Niek on 3-6-2018.
 */

public class DrawView extends GLSurfaceView {
    public DrawView(Context context) {
        super(context);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

//    private static KeyboardHandler softKeyboardHandler;
//    private final static int SHOW_IME_KEYBOARD = 0;
//    private final static int HIDE_IME_KEYBOARD = 1;
//    private static EditText editText;
//    private static InputMethodManager imm;
//
//    public DrawView(Context context) {
//        super(context);
//        editText = new EditText(context);
//        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        softKeyboardHandler = new KeyboardHandler();
//    }
//
//    public static void showIMEKeyboard() {
//        softKeyboardHandler.sendEmptyMessage(SHOW_IME_KEYBOARD);
//    }
//
//    public static void hideImeKeyboard() {
//        softKeyboardHandler.sendEmptyMessage(HIDE_IME_KEYBOARD);
//    }

//    private static class KeyboardHandler extends Handler {
//        public void handleMessage(Message msg) {
//            switch(msg.what) {
//                case SHOW_IME_KEYBOARD:
//                    editText.requestFocus();
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//                    System.out.println("SHOW KEYBOARD");
//                    break;
//                case HIDE_IME_KEYBOARD:
//                    imm.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);
//                    System.out.println("HIDE KEYBOARD");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}
