package knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Drawable;
import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Text;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.Matrices;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;

/**
 * Created by Niek on 28-5-2018.
 */

public class TextBox extends LayoutBox {
    private Text textDraw = null;
    private boolean stayInsideBox = true;
    private boolean breakOnSpace = true;
    private String text = null;
    private TextManager textManager = null;
    float[] textColor = null;
    private boolean first = true;


    public TextBox(LayoutManager manager, LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(manager, parent, left, right, bottom, top, relative);
    }

    public void setText(TextManager manager, String text, float[] color) {
        this.textManager = manager;
        this.text = text;
        this.textColor = color;
    }

    @Override
    public void initAll() {
        super.init();
        if (text != null) {
            if (!textManager.isFontLoaded()) {
                textManager.load(manager.getUnit());
            }

            textDraw = textManager.getTextFit(text, 0, 0, textColor, width, height, stayInsideBox, breakOnSpace);
            if (textDraw != null) {
                textDraw.setTransformMatrix(Matrices.getTranslationMatrix(left, top));
                textDraw .setReady(true);
            }
        }

        super.initChilderen();
    }

    @Override
    protected void resolutionChain() {
        super.resolutionMe();
        if (text != null && textDraw != null) {
            Text newText = textManager.getTextFit(text, 0, 0, textColor, width, height, stayInsideBox, breakOnSpace);
            if (newText != null) {
                textDraw.editPoints(newText.getPoints());
                textDraw.editTexels(newText.getTexcoords());
                textDraw.setTransformMatrix(Matrices.getTranslationMatrix(left, top));
            } else {
                textDraw.setReady(false);
            }
        }

        super.resolutionChilderen();
    }

    @Override
    public ArrayList<Drawable> toDrawable(boolean edges) {
        ArrayList<Drawable> returnDrawables = super.toDrawable(edges);
        if (first) {
            returnDrawables.add(textDraw);
            first = false;
        }

        return returnDrawables;
    }

    public Text getText() {
        return textDraw;
    }

    public void setStayInsideBox(boolean stayInsideBox) {
        this.stayInsideBox = stayInsideBox;
    }
}
