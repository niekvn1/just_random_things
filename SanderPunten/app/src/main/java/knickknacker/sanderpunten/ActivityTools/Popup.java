package knickknacker.sanderpunten.ActivityTools;

import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchCallback;

public class Popup {
    private final String POPUP_TEXT_KEY = "popup_text_key";
    private final String POPUP_BUTTON_TEXT_KEY = "popup_button_text_key";
    private final int STATE_CODE;
    private LayoutManager layoutManager;


    public Popup(LayoutManager layoutManager, int code) {
        this.layoutManager = layoutManager;
        STATE_CODE = code;
        layoutManager.newLayout(STATE_CODE);
        init();
    }

    private void init() {
        LayoutBox popup_root = layoutManager.getLayout(STATE_CODE);
        popup_root.setColor(Colors.BLACK_ALPHA_6);
        popup_root.setzIndex(-0.9f);

        LayoutBox popup = new LayoutBox(layoutManager, popup_root, 0.0f, 1.0f, 0.25f, 0.75f, true);
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

    public void setText(TextManager msgM, TextManager buttonM, String msgT, String buttonT) {
        TextBox textbox = (TextBox) layoutManager.getDirectAccess(POPUP_TEXT_KEY);
        TextBox ok = (TextBox) layoutManager.getDirectAccess(POPUP_BUTTON_TEXT_KEY);

        textbox.setTextManager(msgM);
        textbox.setText(msgT);

        ok.setTextManager(buttonM);
        ok.setText(buttonT);
    }
}
