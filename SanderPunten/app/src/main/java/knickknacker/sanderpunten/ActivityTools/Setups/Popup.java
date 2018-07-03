package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;

public class Popup extends LayoutSetup {
    private final String POPUP_TEXT_KEY = "popup_text_key";
    private final String POPUP_BUTTON_TEXT_KEY = "popup_button_text_key";
    private final int STATE_CODE;

    public Popup(Context context, LayoutManager layoutManager, int code) {
        super(context, layoutManager, layoutManager.newLayout(code), 2);
        STATE_CODE = code;
    }

    public void setup() {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(30);
        layoutManager.loadFont(fonts[0]);

        fonts[1] = new TextManager(context.getAssets());
        fonts[1].setFontFile("font/well_bred.otf");
        fonts[1].setSize(45);
        layoutManager.loadFont(fonts[1]);

        root.setColor(Colors.BLACK_ALPHA_6);
        root.setzIndex(-0.9f);

        LayoutBox popup = new LayoutBox(layoutManager, root, 0.0f, 1.0f, 0.25f, 0.75f, true);
        popup.setBackgroundTexture(layoutManager.getTextures()[6]);
        popup.setColor(Colors.RED_5_ALPHA_6);

        TextBox textbox = new TextBox(layoutManager, popup, 0.05f, 0.95f, 0.35f, 0.95f, true);
        textbox.setColor(Colors.TRANS);
        layoutManager.addDirectAccess(textbox, POPUP_TEXT_KEY);

        Button button = new Button(layoutManager, popup, 0.05f, 0.95f, 0.05f, 0.30f, true);
        button.setColor(Colors.BLACK_ALPHA_6);
        button.setHitColor(Colors.GRAY_ALPHA_6);

        TextBox ok = new TextBox(layoutManager, button, 0.05f, 0.95f, 0.05f, 0.95f, true);
        ok.setColor(Colors.TRANS);
        layoutManager.addDirectAccess(ok, POPUP_BUTTON_TEXT_KEY);

        button.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                layoutManager.unload(STATE_CODE);
            }
        });
    }

    public void setText(String msgT, String buttonT) {
        TextBox textbox = (TextBox) layoutManager.getDirectAccess(POPUP_TEXT_KEY);
        TextBox ok = (TextBox) layoutManager.getDirectAccess(POPUP_BUTTON_TEXT_KEY);

        textbox.setTextManager(fonts[0]);
        textbox.setText(msgT);

        ok.setTextManager(fonts[1]);
        ok.setText(buttonT);
    }
}
