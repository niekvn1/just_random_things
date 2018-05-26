package knickknacker.sanderpunten.Mechanics;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Niek on 22-5-2018.
 */

public class TouchListener implements View.OnTouchListener {

    public boolean onTouch(View view, MotionEvent event) {
        int pointerCount = event.getPointerCount();

        Bundle bundle = new Bundle();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("WOLLLAAAA");
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
//                event.getX();
//                event.getY();
//                event.getPointerId(event.getActionIndex());
                break;
        }


        return true;
    }
}
