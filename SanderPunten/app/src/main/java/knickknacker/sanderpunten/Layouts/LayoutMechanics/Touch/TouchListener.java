package knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;

/**
 * Created by Niek on 22-5-2018.
 */

public class TouchListener implements View.OnTouchListener {
    public static final byte TOUCH_DOWN = 0;
    public static final byte TOUCH_UP = 1;
    public static final byte TOUCH_MOVE = 2;
    public static final byte TOUCH_CANCEL = 3;

    private LayoutManager layoutManager;
    private LayoutBox root;
    private TouchData data;

    public TouchListener(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        root = layoutManager.getRoot();
        data = new TouchData();
    }

    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getX() / layoutManager.getWidth();
        float y = 1.0f - event.getY() / layoutManager.getHeight();
        int pointerID = event.getPointerId(event.getActionIndex());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                Log.i("Down", "x: " + x + " y: " + y + " id: " + pointerID);

                data.addPointer(pointerID, x, y);
                data.setType(TOUCH_DOWN);
                break;
            case MotionEvent.ACTION_UP:
//                System.out.println("[Up] x: " + x + " y: " + y + " id: " + pointerID);

                data.removePointer(pointerID);
                data.setType(TOUCH_UP);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
//                System.out.println("[Down2] x: " + x + " y: " + y + " id: " + pointerID);

                data.addPointer(pointerID, x, y);
                data.setType(TOUCH_DOWN);
                break;
            case MotionEvent.ACTION_POINTER_UP:
//                System.out.println("[Up2] x: " + x + " y: " + y + " id: " + pointerID);

                data.removePointer(pointerID);
                data.setType(TOUCH_UP);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i("Move", "x: " + x + " y: " + y + " id: " + pointerID);
                for (int id : data.getPointerIDs()) {
                    if (id >= 0) {
//                        Log.i("FOR", "x: " + event.getX(event.findPointerIndex(id)) / layoutManager.getWidth() + " y: " + event.getY(event.findPointerIndex(id)) / layoutManager.getHeight() + " id: " + id);
                        data.setX(id, event.getX(event.findPointerIndex(id)) / layoutManager.getWidth());
                        data.setY(id, 1.0f - event.getY(event.findPointerIndex(id)) / layoutManager.getHeight());
                    }
                }

                data.setType(TOUCH_MOVE);
                break;
            case MotionEvent.ACTION_CANCEL:
                data.setType(TOUCH_CANCEL);
                data.cancel();
                break;
        }

        Log.i("ID", data.getPointerIDs()[0] + " " + data.getPointerIDs()[1] + " " + data.getPointerIDs()[2] + " " + data.getPointerIDs()[3] + " " + data.getPointerIDs()[4] + " ");

        this.data.setIdOfInterest(pointerID);
        root.onTouchEvent(this.data);
        return true;
    }

    public void setRoot(LayoutBox root) {
        this.root = root;
    }

    public class TouchData {
        private byte MAX_POINTERS = 5;
        private int pointerCount;
        private float[] x = new float[MAX_POINTERS];
        private float[] y = new float[MAX_POINTERS];
        private int[] pointerIDs = new int[MAX_POINTERS];
        private byte type;
        private int idOfInterest = -1;

        public TouchData() {
            pointerCount = 0;
            for (int i = 0; i < pointerIDs.length; i++) {
                pointerIDs[i] = -1;
            }
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }

        public void addPointer(int pointerID, float x, float y) {
            if (pointerID < MAX_POINTERS) {
                this.pointerIDs[pointerID] = pointerID;
                this.x[pointerID] = x;
                this.y[pointerID] = y;
                pointerCount++;
            }
        }

        public void removePointer(int pointerID) {
            if (pointerID < MAX_POINTERS) {
                pointerIDs[pointerID] = -1;
                pointerCount--;
            }
        }

        public void cancel() {
            pointerCount = 0;
            idOfInterest = -1;
            for (int i = 0; i < pointerIDs.length; i++) {
                pointerIDs[i] = -1;
            }
        }

        public void setX(int i, float x) {
            this.x[i] = x;
        }

        public void setY(int i, float y) {
            this.y[i] = y;
        }

        public float getX(int i) {
            return x[i];
        }

        public float getY(int i) {
            return y[i];
        }

        public float getX() {
            return x[idOfInterest];
        }

        public float getY() {
            return y[idOfInterest];
        }

        public int[] getPointerIDs() {
            return pointerIDs;
        }

        public int getPointerCount() {
            return pointerCount;
        }

        public int getIdOfInterest() {
            return idOfInterest;
        }

        public void setIdOfInterest(int idOfInterest) {
            this.idOfInterest = idOfInterest;
        }
    }
}
