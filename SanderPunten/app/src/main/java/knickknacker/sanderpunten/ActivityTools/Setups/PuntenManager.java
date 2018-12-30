package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import knickknacker.opengldrawables.Drawing.Properties.Colors;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.List;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;
import knickknacker.shared.Signables.PublicUserData;

public class PuntenManager extends LayoutSetup {
    private PuntenManagerCallback callback;
    private List list;
    private ArrayList<User> users;
    private int myId;

    public PuntenManager(PuntenManagerCallback callback, LayoutManager layoutManager, Layout layout, int myId) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
        this.myId = myId;
        users = new ArrayList<>();
    }

    public void setup() {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(35);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[8]);

        list = new List(root, 0.05f, 0.95f, 0.15f, 0.95f, true);
        list.setMargin(20f);
        list.setColor(Colors.WHITE_ALPHA_6);

        Button button = new Button(root, 0.05f, 0.95f, 0.05f, 0.14f, true);
        TextBox text = new TextBox(button, 0.05f, 0.95f, 0.05f, 0.95f, true);
        text.setTextManager(fonts[0]);
        button.setColor(Colors.BLUE);
        button.setHitColor(Colors.GREEN);
        button.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                commit();
            }
        });
        text.setText("Save Changes", Colors.WHITE);

        callback.getUsers();
    }

    public void setUsers(ArrayList users, boolean reload) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof PublicUserData) {
                PublicUserData data = (PublicUserData) users.get(i);
                Log.i("Name", data.getName());
                User user = findUser(data.getId());
                if (user == null) {
                    initUser(data);
                } else {
                    editUser(user, data);
                }

                if (data.getId() == myId) {
                    callback.addedSanderPuntenMe(data.getSanderpunten());
                }
            }
        }

        if (reload) {
            layout.reload();
        }
    }

    private void editUser(User user, PublicUserData data) {
        user.getPublicUserData().setSanderpunten(data.getSanderpunten());
        setPunten(user.getPunten(), data);
    }

    private void initUser(PublicUserData user) {
        LayoutBox entry;
        TextBox name;
        Button plus;
        Button minus;
        TextBox punten;

        entry = new LayoutBox(list, 850, 150, false);
        entry.setColor(Colors.GRAY_ALPHA_6);

        name = new TextBox(entry, 0.01f, 0.5f, 0.05f, 0.95f, true);
        name.setTextManager(fonts[0]);
        name.setColor(Colors.TRANS);
        name.setText(user.getName(), Colors.WHITE);

        minus = new Button(entry, 0.51f, 0.65f, 0.05f, 0.95f, true);
        minus.setBackgroundTexture(layoutManager.getTextures()[10]);
        if (callback.isAdmin()) {
            minus.setColor(Colors.WHITE);
            minus.setHitColor(Colors.RED);
            minus.setTouchCallback(new TouchCallback() {
                @Override
                public void onTouch(LayoutBox box) {
                    removeSanderPunt((Button) box);
                }
            });
        } else {
            minus.setColor(Colors.GRAY_ALPHA_6);
        }

        plus = new Button(entry, 0.85f, 0.99f, 0.05f, 0.95f, true);
        plus.setBackgroundTexture(layoutManager.getTextures()[9]);
        if (callback.isAdmin() || user.getId() != myId) {
            plus.setColor(Colors.WHITE);
            plus.setHitColor(Colors.GREEN);
            plus.setTouchCallback(new TouchCallback() {
                @Override
                public void onTouch(LayoutBox box) {
                    addSanderPunt((Button) box);
                }
            });
        } else {
            plus.setColor(Colors.GRAY_ALPHA_6);
        }


        punten = new TextBox(entry, 0.66f, 0.84f, 0.05f, 0.95f, true);
        punten.setTextManager(fonts[0]);
        setPunten(punten, user);

        this.users.add(new User(user, entry, name, plus, minus, punten));
    }

    private void commit() {
        ArrayList<PublicUserData> data = new ArrayList<>();
        PublicUserData userData;
        for (User user : users) {
            if (user.getAdded() != 0) {
                userData = new PublicUserData(user.getPublicUserData().getId());
                userData.setSanderpunten(user.getAdded());
                data.add(userData);

                user.setAdded(0);
            }
        }

        if (data.size() > 0) {
            callback.addedSanderPunten(data);
        }
    }

    private void addSanderPunt(Button plus) {
        int found = 0;
        for (User user : users) {
            if (user.getPlus() == plus) {
                user.getPublicUserData().setSanderpunten(user.getPublicUserData().getSanderpunten() + 1);
                user.setAdded(user.getAdded() + 1);
                setPunten(user.getPunten(), user.getPublicUserData());
                if (user.getPublicUserData().getId() == myId) {
                    break;
                } else {
                    found++;
                }
            }

            if (user.getPublicUserData().getId() == myId) {
                user.getPublicUserData().setSanderpunten(user.getPublicUserData().getSanderpunten() - 1);
                user.setAdded(user.getAdded() - 1);
                setPunten(user.getPunten(), user.getPublicUserData());
                found++;
            }

            if (found > 1) {
                break;
            }
        }

        layout.reload();
    }

    private void removeSanderPunt(Button minus) {
        for (User user : users) {
            if (user.getMinus() == minus) {
                user.getPublicUserData().setSanderpunten(user.getPublicUserData().getSanderpunten() - 1);
                user.setAdded(user.getAdded() - 1);
                setPunten(user.getPunten(), user.getPublicUserData());
                break;
            }
        }

        layout.reload();
    }

    private User findUser(int id) {
        for (User user : users) {
            if (user.getPublicUserData().getId() == id) {
                return user;
            }
        }

        return null;
    }

    private void setPunten(TextBox punten, PublicUserData user) {
        if (user.getSanderpunten() < 0) {
            punten.setText("" + user.getSanderpunten(), Colors.RED);
        } else if (user.getSanderpunten() == 0) {
            punten.setText("" + user.getSanderpunten(), Colors.WHITE);
        } else {
            punten.setText("" + user.getSanderpunten(), Colors.GREEN);
        }
    }

    private class User {
        private PublicUserData publicUserData;
        private LayoutBox entry;
        private TextBox name;
        private Button plus;
        private Button minus;
        private TextBox punten;
        private int added = 0;

        public User(PublicUserData publicUserData, LayoutBox entry, TextBox name, Button plus, Button minus, TextBox punten) {
            this.publicUserData = publicUserData;
            this.entry = entry;
            this.name = name;
            this.plus = plus;
            this.minus = minus;
            this.punten = punten;
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

        public Button getPlus() {
            return plus;
        }

        public void setPlus(Button plus) {
            this.plus = plus;
        }

        public Button getMinus() {
            return minus;
        }

        public void setMinus(Button minus) {
            this.minus = minus;
        }

        public TextBox getPunten() {
            return punten;
        }

        public void setPunten(TextBox punten) {
            this.punten = punten;
        }

        public int getAdded() {
            return added;
        }

        public void setAdded(int added) {
            this.added = added;
        }
    }
}
