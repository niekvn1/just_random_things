package knickknacker.sanderpunten.LayoutMechanics.Objects;

/**
 * Created by Niek on 22-5-2018.
 */

public class ButtonMenu extends LayoutBox {
    private float buttonMargin;
    private float buttonSize;
    private int buttonCount;
    private boolean buttonsSet = false;
    private boolean fit;
    float[] buttonColor;
    int buttonTexture = -1;

    public ButtonMenu(LayoutBox parent, float left_, float right_, float bottom_, float top_, boolean relative, boolean fit, int buttonCount) {
        super(parent, left_, right_, bottom_, top_, relative);
        this.fit = fit;
        this.buttonCount = buttonCount;
        for (int i = 0; i < buttonCount; i++) {
            new Button(this);
        }
    }

    @Override
    public void initAll() {
        super.init();
        if (fit) {
            exactFit();
        }
    }

    @Override
    public void newResolution(float left, float right, float bottom, float top) {

    }

    private boolean exactFit() {
        if (!buttonsSet) {
            float button_height = (height - (buttonCount - 1) * buttonMargin) / buttonCount;
            for (int i = 0; i < buttonCount; i++) {
                float[] corners = new float[4];
//                left, right, bottom, top
                corners[0] = left;
                corners[1] = right;
                corners[2] = top - i * buttonMargin - (i + 1) * button_height;
                corners[3] = top - i * buttonMargin - i * button_height;

                LayoutBox button = childeren.get(i);
                button.setColor(buttonColor);
                button.setBackgroundTexture(buttonTexture);
                button.initAll(corners[0], corners[1], corners[2], corners[3]);
            }

            buttonsSet = true;
            return true;
        }

        return false;
    }

    public float getButtonMargin() {
        return buttonMargin;
    }

    public void setButtonMargin(float buttonMargin) {
        this.buttonMargin = buttonMargin;
    }

    public float getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(float buttonSize) {
        this.buttonSize = buttonSize;
    }

    public int getButtonCount() {
        return buttonCount;
    }

    public void setButtonColor(float[] buttonColor) {
        this.buttonColor = buttonColor;
    }

    public void setButtonTexture(int buttonTexture) {
        this.buttonTexture = buttonTexture;
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
