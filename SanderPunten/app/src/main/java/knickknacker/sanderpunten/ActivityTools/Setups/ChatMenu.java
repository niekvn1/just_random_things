package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;

public class ChatMenu extends LayoutSetup {

    public ChatMenu(Context context, LayoutManager layoutManager, LayoutBox root) {
        super(context, layoutManager, root, 1);
    }

    public void setup() {
        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(25);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[5]);
        root.setColor(Colors.WHITE);
    }
}
