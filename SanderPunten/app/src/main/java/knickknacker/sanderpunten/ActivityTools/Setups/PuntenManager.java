package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import knickknacker.opengldrawables.Drawing.Properties.Colors;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.List;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.tcp.Signables.PublicUserData;

public class PuntenManager extends LayoutSetup {
    private PuntenManagerCallback callback;
    private List list;
    private ArrayList<User> users;

    public PuntenManager(PuntenManagerCallback callback, LayoutManager layoutManager, Layout layout) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
        users = new ArrayList<>();
    }

    public void setup() {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(35);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[8]);

        list = new List(root, 0.05f, 0.95f, 0.05f, 0.95f, true);
        list.setMargin(20f);
        list.setColor(Colors.WHITE_ALPHA_6);

        callback.getUsers();
    }

    public void setUsers(ArrayList users) {
        LayoutBox entry;
        TextBox name;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof PublicUserData) {
                PublicUserData user = (PublicUserData) users.get(i);
                Log.i("Name", user.getName());
                entry = new LayoutBox(list, 900, 150, false);
                entry.setColor(Colors.GRAY_ALPHA_6);
                Log.i("Width", "" + fonts[0]);
                name = new TextBox(entry, 0.01f, 0.7f, 0.05f, 0.95f, true);
                name.setTextManager(fonts[0]);
                name.setColor(Colors.TRANS);
                name.setText(user.getName(), Colors.WHITE);

                this.users.add(new User(user, entry, name));
            }
        }

        layout.reload();
    }

    private class User {
        private PublicUserData publicUserData;
        private LayoutBox entry;
        private TextBox name;

        public User(PublicUserData publicUserData, LayoutBox entry, TextBox name) {
            this.publicUserData = publicUserData;
            this.entry = entry;
            this.name = name;
        }

        public PublicUserData getPublicUserData() {
            return publicUserData;
        }

        public void setPublicUserData(PublicUserData publicUserData) {
            this.publicUserData = publicUserData;
        }

        public LayoutBox getEntry() {
            return entry;
        }

        public void setEntry(LayoutBox entry) {
            this.entry = entry;
        }

        public TextBox getName() {
            return name;
        }

        public void setName(TextBox name) {
            this.name = name;
        }
    }
}
