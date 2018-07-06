package knickknacker.sanderpunten.ActivityTools.Setups;

import android.content.Context;
import android.util.Log;

import knickknacker.sanderpunten.ActivityTools.LayoutSetup;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.List;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBar;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBarCallback;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.Touch.TouchCallback;
import knickknacker.opengldrawables.Drawing.Properties.Colors;
import knickknacker.opengldrawables.Drawing.Tools.TextManager;

public class ChatMenu extends LayoutSetup implements TextBarCallback {
    private ChatMenuCallback callback;
    private TextBar insert;
    private List chatLog;

    public ChatMenu(ChatMenuCallback callback, LayoutManager layoutManager, Layout layout) {
        super((Context) callback, layoutManager, layout, 1);
        this.callback = callback;
    }

    public void setup() {
        LayoutBox root = layout.getRoot();

        fonts[0] = new TextManager(context.getAssets());
        fonts[0].setFontFile("font/well_bred.otf");
        fonts[0].setSize(25);
        layoutManager.loadFont(fonts[0]);

        root.setBackgroundTexture(layoutManager.getTextures()[5]);
        root.setColor(Colors.WHITE);

        chatLog = new List(root, 0.05f, 0.95f, 0.20f, 0.95f, true);
        chatLog.setColor(Colors.WHITE_ALPHA_6);
        chatLog.setMargin(20);

        LayoutBox chatBar = new LayoutBox(root, 0.05f, 0.95f, 0.05f, 0.15f, true);
        chatBar.setColor(Colors.WHITE_ALPHA_6);

        insert = new TextBar(chatBar, 0.05f, 0.80f, 0.05f, 0.95f, true);
        insert.setColor(Colors.GRAY_ALPHA_6);
        insert.setTextManager(fonts[0]);
        insert.setCallback(this);

        Button send = new Button(chatBar, 0.85f, 0.95f, 0.05f, 0.95f, true);
        send.setColor(Colors.WHITE);
        send.setBackgroundTexture(layoutManager.getTextures()[7]);
        send.setHitColor(Colors.WHITE_ALPHA_6);
        send.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                onTextCommitted(insert);
            }
        });

        for (int i = 1; i < 101; i++) {
            TextBox entry = new TextBox(chatLog, 500f, 100, false);
            entry.setTextManager(fonts[0]);
            entry.setColor(Colors.GRAY_ALPHA_6);
            entry.setText("test " + i);
        }
    }

    @Override
    public void onTextCommitted(TextBar textBar) {
        if (textBar.getText() != "") {
            callback.onChatSend(textBar.getText());
            textBar.setText("");
        }
    }

    public void onChatReceive(String msg) {
        TextBox entry = new TextBox(chatLog, chatLog.getWidth() * 0.9f, 100, false);
        entry.setTextManager(fonts[0]);
        entry.setColor(Colors.GRAY_ALPHA_6);
        entry.setText(msg);
        Log.i("CHATMENU", "size: " + chatLog.getChildCount());
        layoutManager.reload(layout);
    }
}
