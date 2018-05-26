package knickknacker.sanderpunten.Drawing.Objects;

import java.util.ArrayList;

/**
 * Created by Niek on 22-5-2018.
 */

public class ButtonMenu extends LayoutBox {
    private float margin;
    private int button_count;
    private ArrayList<float[]> buttons;

    public ButtonMenu(LayoutBox parent, int button_count_, float left_, float right_, float bottom_, float top_, float margin_) {
        super(parent, left_, right_, bottom_, top_);
        this.button_count = button_count_;
        this.margin = margin_;

        buttons = new ArrayList<>();
        float button_height = (2.0f - top - bottom - (button_count - 1) * margin) / button_count;
        for (int i = 0; i < button_count; i++) {
            float[] corners = new float[8];
//                left, bottom
            corners[0] = -1 + left;
            corners[1] = 1.0f - top - i * margin - (i + 1) * button_height;

//                left, top
            corners[2] = -1 + left;
            corners[3] = 1.0f - top - i * margin - i * button_height;

//                right, bottom
            corners[4] = 1.0f - right;
            corners[5] = 1.0f - top - i * margin - (i + 1) * button_height;

//                right, top
            corners[6] = 1.0f - right;
            corners[7] = 1.0f - top - i * margin - i * button_height;

            buttons.add(corners);
        }
    }

    public ArrayList<float[]> getButtonsPoints() {
        return buttons;
    }

    public String toString() {
        String output = "";
        for (float[] points : buttons) {
            for (int i = 0; i < 8; i+= 2) {
                output += (points[i] + "," + points[i + 1] + " | ");
            }

            output += "\n";
        }

        return output;
    }
}
