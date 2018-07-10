package knickknacker.sanderpunten.ActivityTools;

import android.content.Context;

import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;

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

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public Layout getLayout() {
        return layout;
    }

    public TextManager[] getFonts() {
        return fonts;
    }

    public Context getContext() {
        return context;
    }
}
