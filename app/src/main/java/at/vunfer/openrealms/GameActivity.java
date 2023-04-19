/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Log.d("Debug", "Starting game activity...");

        // this hostname is used for the Emulator!
        ClientThread clientThread = new ClientThread(8040, "10.0.2.2");
        clientThread.start();

        TextView textView = findViewById(R.id.exampleView);

        Button btn = findViewById(R.id.exampleBtn);
        btn.setOnClickListener(
                view -> {
                    clientThread.sendStringToServer();
                    textView.setText(clientThread.getFromServer());
                });
    }
}
