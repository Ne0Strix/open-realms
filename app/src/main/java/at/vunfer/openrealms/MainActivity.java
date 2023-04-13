/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.DeckGenerator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Card> deck = DeckGenerator.generateMarketDeck(getApplicationContext());
        Log.i("DeckGenerator", deck.toString());
    }
}
