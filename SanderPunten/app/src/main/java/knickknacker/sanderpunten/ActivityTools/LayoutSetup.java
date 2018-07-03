package knickknacker.sanderpunten.ActivityTools;

import android.content.Context;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;

public class LayoutSetup {
    protected LayoutManager layoutManager;
    protected Layout layout;
    protected TextManager[] fonts;
    protected Context context;

    public LayoutSetup(Context context, LayoutManager layoutManager, Layout layout, int fontCount) {
        this.context = context;
        this.layoutManager = layoutManager;
        this.layout = layout;
        fonts = new TextManager[fontCount];
    }
}
