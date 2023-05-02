/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.client.ClientConnector;
import at.vunfer.openrealms.network.server.ServerThread;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements UIUpdateListener {
    private static final String TAG = "MainActivity";
    private final int connectionPort = 1337;
    private String connectionIP;
    private ServerThread server;
    private ClientConnector connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }

    public void hostGame(View view) {
        setContentView(R.layout.host);
    }

    public void joinGame(View view) {
        setContentView(R.layout.join);
    }

    public void toMainMenu(View view) throws IOException {
        setContentView(R.layout.menu);
        if (server != null) {
            server.stopServer();
        }
    }

    public void startServer(View view) throws InterruptedException {
        server = new ServerThread(this, connectionPort);
        TextView showIpPrompt = (TextView) findViewById(R.id.prompt_text);
        Button openLobbyButton = (Button) findViewById(R.id.openLobby);
        Button showIpButton = (Button) findViewById(R.id.showIp);

        showIpPrompt.setText("Tap the button below to get your IP address.");

        openLobbyButton.setVisibility(View.GONE);
        showIpButton.setVisibility(View.VISIBLE);

        server.start();
    }

    public void showIp(View view) throws IOException, InterruptedException {
        TextView showIp = (TextView) findViewById(R.id.prompt_text);
        Button button = (Button) findViewById(R.id.showIp);
        Button startButton = (Button) findViewById(R.id.startGame);

        connectionIP = server.getIpAddr();

        button.setVisibility(View.GONE);
        showIp.setText("Your IP address is:\n" + connectionIP);
        startButton.setVisibility(View.VISIBLE);

        connection = new ClientConnector(this);
        connection.setConnectionTarget(connectionIP, connectionPort);
        connection.start();
    }

    public void connectServer(View view) throws IOException, InterruptedException {
        EditText getIp = (EditText) findViewById(R.id.get_text);
        connectionIP = getIp.getText().toString();
        Log.i(TAG, "Connecting to IP: " + connectionIP);
        connection = new ClientConnector(this);

        connection.setConnectionTarget(connectionIP, connectionPort);
        connection.start();
    }

    public void startGame(View view) throws IOException {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void updateUI(Message message) {
        TextView target = findViewById(R.id.message_display);
        runOnUiThread(() -> target.setText(message.getType().toString()));
    }
}
