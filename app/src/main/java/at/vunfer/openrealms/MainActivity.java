/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.client.ClientConnector;
import at.vunfer.openrealms.network.server.ServerThread;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements UIUpdateListener {

    private int connectionPort = 1234;

    private ServerThread server = new ServerThread(connectionPort);
    private ClientConnector connection;
    private final int port = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_test);
        server.start();
    }

    public void connectServer(View view) throws IOException, InterruptedException {
        EditText input = (EditText) findViewById(R.id.get_ip);
        String readIpAddr = input.getText().toString();

        TextView hostip = findViewById(R.id.hostip);

        connection = new ClientConnector(this);
        connection.setConnectionTarget("192.168.42.57", connectionPort);
        connection.start();
    }

    public void sendMessage(View view) throws IOException {
        EditText input = (EditText) findViewById(R.id.get_text);
        String text = input.getText().toString();
        Message msg = new Message(MessageType.TOUCHED);
        connection.sendMessage(msg);
    }

    @Override
    public void updateUI(Message message) {
        TextView target = findViewById(R.id.message_display);
        runOnUiThread(() -> target.setText(message.getType().toString()));
    }
}
