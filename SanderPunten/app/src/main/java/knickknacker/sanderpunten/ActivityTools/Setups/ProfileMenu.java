package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;
import android.util.Log;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBar;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBarCallback;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;

public class ProfileMenu extends LayoutSetup implements TextBarCallback {
    private final String TEXTBAR_NAME = "textbar_name";
    private ProfileMenuCallback callback;

    public ProfileMenu(ProfileMenuCallback callback, LayoutManager layoutManager, Layout layout) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
    }

    public void setup(String username) {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(35);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[3]);
        root.setColor(Colors.WHITE);

        LayoutBox upper = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.60f, 0.95f, true);
        LayoutBox bottom = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.05f, 0.50f, true);
        upper.setColor(Colors.WHITE_ALPHA_6);
        bottom.setColor(Colors.WHITE_ALPHA_6);

        TextBox pre_name = new TextBox(layoutManager, bottom, 0.05f, 0.25f, 0.8f, 0.95f, true);
        pre_name.setColor(Colors.GRAY_ALPHA_6);
        pre_name.setTextManager(fonts[0]);
        pre_name.setText("Name: ", Colors.BLACK);

        TextBar name = new TextBar(layoutManager, bottom, 0.30f, 0.95f, 0.8f, 0.95f, true);
        name.setColor(Colors.GRAY_ALPHA_6);
        name.setTextManager(fonts[0]);
        name.setText(username, Colors.BLACK);
        layoutManager.addDirectAccess(name, TEXTBAR_NAME);
        name.setCallback(this);
    }

    public void onTextCommitted(TextBar textBar) {
        switch (textBar.getId()) {
            case TEXTBAR_NAME:
                callback.changedName(textBar.getText());
                break;
        }
    }

    public void changeName(String name) {
        Log.i("PROFILE", "Setting name to " + name);
        TextBar textBar = (TextBar) layoutManager.getDirectAccess(TEXTBAR_NAME);
        textBar.setText(name);
    }
}
