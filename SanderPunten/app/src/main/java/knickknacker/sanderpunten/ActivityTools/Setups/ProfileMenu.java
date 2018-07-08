package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;
import android.util.Log;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBar;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBarCallback;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.opengldrawables.Drawing.Properties.Colors;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;

public class ProfileMenu extends LayoutSetup implements TextBarCallback {
    private final String TEXTBAR_NAME = "textbar_name";
    private final String TEXTBARE_PASSWORD = "textbare_password";
    private ProfileMenuCallback callback;
    private TextBar password;
    Button admin;
    TextBox puntenText;
    TextBox idText;

    public ProfileMenu(ProfileMenuCallback callback, LayoutManager layoutManager, Layout layout) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
    }

    public void setup(String username, int id, boolean isAdmin, long punten) {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(35);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[3]);
        root.setColor(Colors.WHITE);

        LayoutBox box = new LayoutBox(root, 0.05f, 0.95f, 0.05f, 0.95f, true);
        box.setColor(Colors.WHITE_ALPHA_6);

        /** Change name: */
        TextBox pre_name = new TextBox(box, 0.05f, 0.25f, 0.87f, 0.95f, true);
        pre_name.setColor(Colors.GRAY_ALPHA_6);
        pre_name.setTextManager(fonts[0]);
        pre_name.setText("Name: ", Colors.BLACK);

        TextBar name = new TextBar(box, 0.30f, 0.95f, 0.87f, 0.95f, true);
        name.setColor(Colors.GRAY_ALPHA_6);
        name.setTextManager(fonts[0]);
        name.setText(username, Colors.BLACK);
        layoutManager.addDirectAccess(name, TEXTBAR_NAME);
        name.setCallback(this);

        /** Show Sanderpunten: */
        TextBox pre_punten = new TextBox(box, 0.05f, 0.45f, 0.77f, 0.85f, true);
        pre_punten.setTextManager(fonts[0]);
        pre_punten.setColor(Colors.GRAY_ALPHA_6);
        pre_punten.setText("Sanderpunten:", Colors.BLACK);

        puntenText = new TextBox(box, 0.50f, 0.95f, 0.77f, 0.85f, true);
        puntenText.setTextManager(fonts[0]);
        puntenText.setColor(Colors.GRAY_ALPHA_6);
        puntenText.setText("" + punten, Colors.BLACK);

        /** Show id: */
        TextBox pre_id = new TextBox(box, 0.05f, 0.25f, 0.66f, 0.76f, true);
        pre_id.setTextManager(fonts[0]);
        pre_id.setColor(Colors.GRAY_ALPHA_6);
        pre_id.setText("ID:", Colors.BLACK);

        idText = new TextBox(box, 0.30f, 0.95f, 0.66f, 0.76f, true);
        idText.setTextManager(fonts[0]);
        idText.setColor(Colors.GRAY_ALPHA_6);
        idText.setText("" + id, Colors.BLACK);

        /** Become Admin: */
        password = new TextBar(box, 0.05f, 0.70f, 0.05f, 0.13f, true);
        password.setColor(Colors.GRAY_ALPHA_6);
        password.setTextManager(fonts[0]);
        layoutManager.addDirectAccess(password, TEXTBARE_PASSWORD);
        password.setCallback(this);

        admin = new Button(box, 0.75f, 0.95f, 0.05f, 0.13f, true);
        if (isAdmin) {
            admin.setColor(Colors.GRAY_ALPHA_6);
        } else {
            admin.setColor(Colors.YELLOW_ALPHA_6);
            admin.setHitColor(Colors.YELLOW);
            admin.setTouchCallback(new TouchCallback() {
                @Override
                public void onTouch(LayoutBox box) {
                    onTextCommitted(password);
                }
            });
        }

        TextBox adminText = new TextBox(admin, 0.05f, 0.95f, 0.05f, 0.95f, true);
        adminText.setTextManager(fonts[0]);
        adminText.setText("Admin", Colors.BLACK);
    }

    public void onTextCommitted(TextBar textBar) {
        switch (textBar.getId()) {
            case TEXTBAR_NAME:
                callback.changedName(textBar.getText());
                break;
            case TEXTBARE_PASSWORD:
                callback.adminApply(textBar.getText());
                textBar.setText("");
                break;
        }
    }

    public void changeName(String name) {
        Log.i("PROFILE", "Setting name to " + name);
        TextBar textBar = (TextBar) layoutManager.getDirectAccess(TEXTBAR_NAME);
        textBar.setText(name);
    }

    public void onAdminApplyResponse() {
        admin.setColor(Colors.GRAY_ALPHA_6);
        admin.setTouchCallback(null);
    }

    public void changePunten(long punten) {
        puntenText.setText("" + punten);
    }
}
