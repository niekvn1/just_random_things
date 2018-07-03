package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;

import java.util.ArrayList;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.FitBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;

public class MainMenu extends LayoutSetup {
    private final String TEXTBOX_NAME = "textview_name";
    private MainMenuCallback callback;

    public MainMenu(MainMenuCallback callback, LayoutManager layoutManager, Layout layout) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
    }

    public void setup(String username, long sanderpunten) {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(35);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[2]);
        root.setColor(Colors.WHITE);

        /** Button Menu: */
        FitBox child = new FitBox(layoutManager, root,0.1f, 0.9f, 0.1f, 0.9f, true, true);
        child.setChildMargin(20f);

        /** Buttons 1: */
        Button button = new Button(layoutManager, child);
        button.setColor(Colors.WHITE_ALPHA_6);
        button.setHitColor(Colors.RED_ALPHA_6);

        TextBox pre_profile = new TextBox(layoutManager, button, 0.1f, 0.9f, 0.66f, 0.95f, true);
        pre_profile.setTextManager(fonts[0]);
        pre_profile.setColor(Colors.BLACK_ALPHA_6);
        pre_profile.setText("PROFILE", Colors.WHITE);

        TextBox pre_name = new TextBox(layoutManager, button, 0.1f, 0.4f, 0.4f, 0.6f, true);
        pre_name.setTextManager(fonts[0]);
        pre_name.setColor(Colors.GRAY_ALPHA_6);
        pre_name.setText("Name:", Colors.WHITE);

        TextBox name = new TextBox(layoutManager, button, 0.5f, 0.9f, 0.4f, 0.6f, true);
        name.setTextManager(fonts[0]);
        name.setColor(Colors.GRAY_ALPHA_6);
        name.setText(username, Colors.WHITE);
        layoutManager.addDirectAccess(name, TEXTBOX_NAME);

        TextBox pre_punten = new TextBox(layoutManager, button, 0.1f, 0.4f, 0.1f, 0.3f, true);
        pre_punten.setTextManager(fonts[0]);
        pre_punten.setColor(Colors.GRAY_ALPHA_6);
        pre_punten.setText("S-punten:", Colors.WHITE);

        TextBox punten = new TextBox(layoutManager, button, 0.5f, 0.9f, 0.1f, 0.3f, true);
        punten.setTextManager(fonts[0]);
        punten.setColor(Colors.GRAY_ALPHA_6);
        punten.setText(String.valueOf(sanderpunten), Colors.WHITE);

        button.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                MainMenu.this.callback.userProfile();
            }
        });

        /** Button 2 */
        Button button2 = new Button(layoutManager, child);
        button2.setColor(Colors.WHITE_ALPHA_6);
        button2.setHitColor(Colors.BLUE_ALPHA_6);

        TextBox pre_chat = new TextBox(layoutManager, button2, 0.1f, 0.9f, 0.66f, 0.95f, true);
        pre_chat.setTextManager(fonts[0]);
        pre_chat.setColor(Colors.BLACK_ALPHA_6);
        pre_chat.setText("CHAT", Colors.WHITE);

        button2.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                MainMenu.this.callback.chat();
            }
        });
    }

    public void changeName(String name) {
        TextBox namebox = (TextBox) layoutManager.getDirectAccess(TEXTBOX_NAME);
        namebox.setText(name);
    }
}
