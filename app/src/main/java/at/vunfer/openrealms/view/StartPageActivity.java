/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.R;

public class StartPageActivity extends AppCompatActivity {
    private Button logoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);

        // Hintergrundbild setzen
        getWindow().setBackgroundDrawableResource(R.drawable.startpage);

        // Logo-Button initialisieren
        logoButton = findViewById(R.id.logoButton);
        logoButton.setBackgroundResource(R.drawable.herorealmslogo);
        logoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
