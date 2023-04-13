/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;
import android.util.Log;

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

    private static final String LogTag = "DeckGenerator";

    public static List<Card> generatePlayerStarterDeck(Context context) {
        XmlPullParser xmlParser = context.getResources().getXml(R.xml.player_starter_deck);
        return generateDeckFromXml(xmlParser);
    }

    public static List<Card> generateMarketDeck(Context context) {
        XmlPullParser xmlParser = context.getResources().getXml(R.xml.market_deck);
        return generateDeckFromXml(xmlParser);
    }

    public static List<Card> generateDeckFromXml(XmlPullParser xmlParser) {
        List<Card> deck = new ArrayList<>();
        try {
            int event = 0;
            while (event != XmlPullParser.END_DOCUMENT) {
                event = xmlParser.next();
                String name = xmlParser.getName();
                Log.v(LogTag, "Tag: " + name);
                if (event == XmlPullParser.START_TAG && name.equals("card")) {
                    int amount = Integer.parseInt(xmlParser.getAttributeValue(0));
                    Log.v(LogTag, "Adding Card " + amount + " Times");
                    Card c = parseCard(xmlParser);
                    Log.v(LogTag, "Finished Card: " + c);
                    for (int i = 0; i < amount; i++) deck.add(c);
                }
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(LogTag, e.getLocalizedMessage());
        }
        return deck;
    }

    private static Card parseCard(XmlPullParser xmlParser)
            throws XmlPullParserException, IOException {
        String cardName = null;
        int cardCost = -1;
        List<Effect> cardEffects = new ArrayList<>();
        Log.v(LogTag, "Starting with Card");
        int event = 0;
        String name;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) {
                switch (name) {
                    case "name":
                        cardName = xmlParser.nextText();
                        Log.v(LogTag, "Added name: " + cardName);
                        break;
                    case "cost":
                        cardCost = Integer.parseInt(xmlParser.nextText());
                        Log.v(LogTag, "Added cost: " + cardCost);
                        break;
                    case "ability":
                        addCardAbility(cardEffects, xmlParser);
                        Log.v(LogTag, "Added ability: " + cardCost);
                        break;
                }
            }
            if (event == XmlPullParser.END_TAG && name.equals("card")) break;
        }
        return new Card(cardName, cardCost, cardEffects);
    }

    private static void addCardAbility(List<Effect> cardEffects, XmlPullParser xmlParser)
            throws XmlPullParserException, IOException {
        Log.v(LogTag, "Start working on ability.");
        int event = 0;
        String name;

        int amount = 0;
        Effect effect;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) { // TODO: make it order independent (somehow)
                if (name.equals("type")) {
                    switch (xmlParser.nextText()) {
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
                    Log.v(LogTag, "Added ability " + effect);
                } else if (name.equals("amount")) {
                    amount = Integer.parseInt(xmlParser.nextText());
                }
            }
            if (event == XmlPullParser.END_TAG && name.equals("ability")) break;
        }
    }
}
