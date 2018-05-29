package knickknacker.sanderpunten.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Drawing.Drawables.Text;
import knickknacker.sanderpunten.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Drawing.Tools.Matrices;

/**
 * Created by Niek on 28-5-2018.
 */

public class TextBox extends LayoutBox {
    ArrayList<Text> texts = new ArrayList<>();
    private boolean stayInsideBox = true;
    private boolean breakOnSpace = true;
    String text = null;
    TextManager manager = null;
    float[] textColor = null;

    public TextBox(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(parent, left, right, bottom, top, relative);
    }

    public void setText(TextManager manager, String text, float[] color) {
        this.manager = manager;
        this.text = text;
        this.textColor = color;
    }

    @Override
    public void initAll() {
        super.init();
        Text textDraw = manager.getTextFit(text, 0, 0, textColor, width, height, stayInsideBox, breakOnSpace);
        textDraw.setTransformMatrix(Matrices.getTranslationMatrix(left, top));
        texts.add(textDraw);
        super.initChilderen();
    }

    @Override
    public ArrayList<Drawable> toDrawable(boolean edges) {
        ArrayList<Drawable> returnDrawables = super.toDrawable(edges);
        for (Text text : texts) {
            returnDrawables.add(text);
        }

        return returnDrawables;
    }

    public ArrayList<Text> getTexts() {
        return texts;
    }

    public void setStayInsideBox(boolean stayInsideBox) {
        this.stayInsideBox = stayInsideBox;
    }
}
