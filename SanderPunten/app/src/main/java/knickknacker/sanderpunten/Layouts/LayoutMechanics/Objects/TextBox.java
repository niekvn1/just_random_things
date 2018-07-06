package knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects;

import java.util.ArrayList;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.opengldrawables.Drawing.Drawables.Drawable;
import knickknacker.opengldrawables.Drawing.Drawables.Text;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;
import knickknacker.opengldrawables.Drawing.Tools.Matrices;

/**
 * Created by Niek on 28-5-2018.
 */

public class TextBox extends LayoutBox {
    private Text textDraw = null;
    private boolean stayInsideBox = true;
    private boolean breakOnSpace = true;
    protected String text = null;
    private TextManager textManager = null;
    float[] textColor = null;
    private boolean first = true;

    public TextBox(Layout layout) {
        super(layout);
    }

    public TextBox(LayoutBox parent) {
        super(parent);
    }

    public TextBox(LayoutBox parent, float width, float height, boolean relative) {
        this(parent, 0f, width , 0f, height, relative);
    }

    public TextBox(LayoutBox parent, float left, float right, float bottom, float top, boolean relative) {
        super(parent, left, right, bottom, top, relative);
    }

    public void setTextManager(TextManager textManager) {
        this.textManager = textManager;
    }

    public void setText(String text) {
        this.text = text;
        if (text != null && textDraw != null) {
            editText();
        }
    }

    public void setText(String text, float[] color) {
        this.textColor = color;
        setText(text);
    }

    @Override
    public void initAll() {
        super.init();
        if (text == null) {
            text = "";
        }

        super.initChilderen();
    }

    private void editText() {
        Text newText = textManager.getTextFit(text, 0, 0, zIndex - 0.0001f, textColor, width, height, stayInsideBox, breakOnSpace);
        if (newText != null) {
            textDraw.editPoints(newText.getPoints());
            textDraw.editTexels(newText.getTexcoords());
            textDraw.setTransformMatrix(Matrices.getTranslationMatrix(left, top));
        } else {
            textDraw.setReady(false);
        }
    }

    @Override
    public ArrayList<Drawable> toDrawable(boolean edges) {
        ArrayList<Drawable> returnDrawables = super.toDrawable(edges);
        if (ignore) {
            return returnDrawables;
        }

        if (textDraw == null) {
            textDraw = textManager.getTextFit(text, 0, 0, zIndex - 0.0001f, textColor, width, height, stayInsideBox, breakOnSpace);
            if (textDraw != null) {
                textDraw.setTransformMatrix(Matrices.getTranslationMatrix(left, top));
                textDraw .setReady(true);
                if (parent != null) {
                    int[] scissors = scissor();
                    textDraw.setScissor(scissors[0], scissors[1], scissors[2], scissors[3]);
                }
            }
        } else {
            editText();
            int[] scissors = scissor();
            textDraw.editScissor(scissors[0], scissors[1], scissors[2], scissors[3]);
        }

        returnDrawables.add(textDraw);
        return returnDrawables;
    }

    @Override
    public void fetchDrawables(ArrayList<Drawable> drawables) {
        if (textDraw != null) {
            drawables.add(textDraw);
        }

        super.fetchDrawables(drawables);
    }

    public Text getTextDraw() {
        return textDraw;
    }

    public String getText() {
        return text;
    }

    public void setStayInsideBox(boolean stayInsideBox) {
        this.stayInsideBox = stayInsideBox;
    }
}
