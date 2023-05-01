/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.client.ClientConnector;
import at.vunfer.openrealms.network.server.ServerThread;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements UIUpdateListener {

    private final int connectionPort = 1234;

    private ServerThread server = new ServerThread(connectionPort);
    private ClientConnector connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_test);
        server.start();
    }

    public void connectServer() throws IOException, InterruptedException {
        connection = new ClientConnector(this);
        connection.setConnectionTarget("192.168.42.57", connectionPort);
        connection.start();
    }

    public void sendMessage() throws IOException {
        Message msg = new Message(MessageType.TOUCHED);
        connection.sendMessage(msg);
    }

    @Override
    public void updateUI(Message message) {
        TextView target = findViewById(R.id.message_display);
        runOnUiThread(() -> target.setText(message.getType().toString()));
    }
}
