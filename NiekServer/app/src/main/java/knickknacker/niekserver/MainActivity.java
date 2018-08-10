package knickknacker.niekserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import knickknacker.niekserver.Networking.RequestHandler;
import knickknacker.niekserver.Networking.WebServer;
import knickknacker.niekserver.Networking.WebServerLog;

public class MainActivity extends AppCompatActivity implements WebServerLog {
    private WebServer webServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webServer = new RequestHandler(this,11223, 2048);
        webServer.setLog(this);
    }

    public void log(String msg) {
        LinearLayout l = findViewById(R.id.log_view);
        TextView t = new TextView(this);
        t.setText(msg);
        l.addView(t);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webServer != null) {
            webServer.onDestroy();
        }
    }
}
