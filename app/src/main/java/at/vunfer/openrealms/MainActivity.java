/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.presenter.*;
import at.vunfer.openrealms.view.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_test);
    }

    public void showCardDetails(View v) {}
}
