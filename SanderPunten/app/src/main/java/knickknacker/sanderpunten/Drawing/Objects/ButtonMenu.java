package knickknacker.sanderpunten.Drawing.Objects;

import java.util.ArrayList;

/**
 * Created by Niek on 22-5-2018.
 */

public class ButtonMenu extends LayoutBox {
    private float margin;
    private int button_count;

    public ButtonMenu(LayoutBox parent, int button_count_, float left_, float right_, float bottom_, float top_, float margin_) {
        super(parent, left_, right_, bottom_, top_);
        this.button_count = button_count_;
        this.margin = margin_;

        float button_height = (height - top - bottom - (button_count - 1) * margin) / button_count;
        for (int i = 0; i < button_count; i++) {
            float[] corners = new float[4];
//                left, right, bottom, top
            corners[0] = 0f + left;
            corners[1] = width - right;
            corners[2] = height - top - i * margin - (i + 1) * button_height;
            corners[3] = height - top - i * margin - i * button_height;


            childeren.add(new LayoutBox(this, corners[0], corners[1], corners[2], corners[3]));
        }
    }

    //TODO: Class must be relative and normal.
//
//    public String toString() {
//        String output = "";
//        for (float[] points : buttons) {
//            for (int i = 0; i < 8; i+= 2) {
//                output += (points[i] + "," + points[i + 1] + " | ");
//            }
//
//            output += "\n";
//        }
//
//        return output;
//    }
}
