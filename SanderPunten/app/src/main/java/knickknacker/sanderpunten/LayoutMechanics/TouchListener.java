package knickknacker.sanderpunten.LayoutMechanics;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import knickknacker.sanderpunten.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 22-5-2018.
 */

public class TouchListener implements View.OnTouchListener {
    private LayoutManager layoutManager;
    private LayoutBox root;

    public TouchListener(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        root = layoutManager.getRoot();
    }

    public boolean onTouch(View view, MotionEvent event) {
        int pointerCount = event.getPointerCount();
        float x = event.getX();
        float y = event.getY();
        int pointerID = event.getPointerId(event.getActionIndex());

        Bundle bundle = new Bundle();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("[Down] x: " + x + " y: " + y);
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("[Up] x: " + x + " y: " + y);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("[Down2] x: " + x + " y: " + y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                System.out.println("[Up2] x: " + x + " y: " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("[Move] x: " + x + " y: " + y);
                break;
        }


        return true;
    }
}
