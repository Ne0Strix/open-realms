package at.vunfer.openrealms.model;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;

public class DeckGenerator {

    private static String LogTag = "DeckGenerator";

    public static List<Card> generatePlayerStarterDeck(Context context) {
        List<Card> deck = new ArrayList<>();
        try {
            XmlPullParser xmlParser = context.getResources().getXml(R.xml.player_starter_deck);

            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                if (xmlParser.getEventType() != XmlPullParser.START_TAG)
                    continue;
                String name = xmlParser.getName();
                if (name.equals("card")) {
                    int amount = Integer.parseInt(xmlParser.getAttributeValue(0));
                    Log.i(LogTag, "Value: " + amount);
                    Card c = parseCard(xmlParser);
                    for (int i = 0; i < amount; i++)
                        deck.add(c);
                }
                Log.i(LogTag, name);
            }

        } catch (IOException | XmlPullParserException e) {
            Log.e("DeckGenerator", e.getLocalizedMessage());
        }
        Log.i(LogTag, deck.toString());
        return deck;
    }

    private static Card parseCard(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        String cardName = null;
        int cardCost = -1;
        List<Effect> cardEffects = new ArrayList<>();
        Log.i("DeckGenerator", "Doing a Card");
        int event = 0;
        String name;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) {
                switch (name) {
                    case "name":
                        cardName = xmlParser.nextText();
                        break;
                    case "cost":
                        cardCost = Integer.parseInt(xmlParser.nextText());
                        break;
                    case "ability":
                        addCardAbility(cardEffects, xmlParser);
                        break;
                }
            }
            if (event == XmlPullParser.END_TAG && name.equals("Card"))
                break;
        }
        return new Card(cardName, cardCost, cardEffects);
    }

    private static void addCardAbility(List<Effect> cardEffects, XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        int event = 0;
        String name;

        int amount = 0;
        Effect effect;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) { //TODO: make it order independent (somehow)
                if (name.equals("type")) {
                    // xmlParser.next();
                    xmlParser.next();
                    switch (xmlParser.getText()) {
                        case "coin":
                            effect = new CoinEffect(amount);
                            break;
                        case "attack":
                            effect = new DamageEffect(amount);
                            break;
                        case "heal":
                            effect = new HealingEffect(amount);
                            break;
                        default:
                            throw new RuntimeException("Ability type not recognized");
                    }
                    cardEffects.add(effect);
                } else if (name.equals("amount")) {
                    xmlParser.next();
                    amount = Integer.parseInt(xmlParser.getText());
                }
            }
            if (event == XmlPullParser.END_TAG && name.equals("ability"))
                break;
        }
    }
}
