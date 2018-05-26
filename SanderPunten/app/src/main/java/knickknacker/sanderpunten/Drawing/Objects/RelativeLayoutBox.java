package knickknacker.sanderpunten.Drawing.Objects;

/**
 * Created by Niek on 27-5-2018.
 */

public class RelativeLayoutBox extends LayoutBox {
    public RelativeLayoutBox(LayoutBox parent) {
        super(parent);
    }

    public RelativeLayoutBox(LayoutBox parent, float left, float right, float bottom, float top) {
        super(parent);
        newResolution(left, right, bottom, top);
    }

    @Override
    public void newResolution(float width, float height) {
        float pwidth = parent.getWidth();
        float pheight = parent.getHeight();
        newResolution(0f, width * pwidth , 0f, height * pheight);
    }

    @Override
    public void newResolution(float left, float right, float bottom, float top) {
        float pwidth = parent.getWidth();
        float pheight = parent.getHeight();
        this.left = left * pwidth;
        this.right = right * pwidth;
        this.bottom = bottom * pheight;
        this.top = top * pheight;

        this.width = this.right - this.left;
        this.height = this.top - this.bottom;
    }
}
