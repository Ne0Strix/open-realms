/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;
import android.util.Log;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.DamagePerChampionInPlayEffect;
import at.vunfer.openrealms.model.effects.DamagePerGuardInPlayEffect;
import at.vunfer.openrealms.model.effects.DrawEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import at.vunfer.openrealms.model.effects.HealingPerChampionInPlayEffect;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DeckGenerator {

    private DeckGenerator() {}

    private static final String LOGGING_TAG = "DeckGenerator";

    public static Deck<Card> generatePlayerStarterDeck(Context context) {
        XmlPullParser xmlParser = context.getResources().getXml(R.xml.player_starter_deck);
        return generateDeckFromXml(xmlParser);
    }

    public static Deck<Card> generateMarketDeck(Context context) {
        XmlPullParser xmlParser = context.getResources().getXml(R.xml.market_deck);
        return generateDeckFromXml(xmlParser);
    }

    public static Deck<Card> generateDeckFromString(String xml) {
        try {
            if (xml == null || xml.isEmpty())
                throw new IllegalArgumentException("Input String must not be null or empty.");
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new ByteArrayInputStream(xml.getBytes()), null);
            return generateDeckFromXml(parser);
        } catch (XmlPullParserException | IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unspecified XmlPullParserException: " + e.getLocalizedMessage());
        }
    }

    public static Deck<Card> generateDeckFromXml(XmlPullParser xmlParser) {
        Deck<Card> deck = new Deck<>();
        try {
            int event = 0;
            while (event != XmlPullParser.END_DOCUMENT) {
                event = xmlParser.next();
                String name = xmlParser.getName();
                Log.v(LOGGING_TAG, "Tag: " + name);
                if (event == XmlPullParser.START_TAG
                        && (name.equals("card") || name.equals("champion"))) {
                    int amount = Integer.parseInt(xmlParser.getAttributeValue(0));
                    Log.v(LOGGING_TAG, "Adding Card " + amount + " Times");
                    Card c = parseCard(xmlParser, name.equals("champion"));
                    Log.v(LOGGING_TAG, "Finished Card: " + c);
                    deck.add(c);
                    if (name.equals("champion"))
                        for (int i = 1; i < amount; i++) deck.add(new Champion((Champion) c));
                    else for (int i = 1; i < amount; i++) deck.add(new Card(c));
                }
            }
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalArgumentException(
                    "Unspecified XmlPullParserException: " + e.getLocalizedMessage());
        }
        return deck;
    }

    private static Card parseCard(XmlPullParser xmlParser, boolean isChampion)
            throws XmlPullParserException, IOException {
        String cardName = null;
        int cardCost = -1;
        CardType cardType = null;
        Faction faction = Faction.NONE;
        List<Effect> cardEffects = new ArrayList<>();
        List<Effect> cardSynergyEffects = new ArrayList<>();
        boolean isGuard = false;
        int health = -1;
        Log.v(LOGGING_TAG, "Starting with Champion");
        int event = 0;
        String name;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) {
                switch (name) {
                    case "name":
                        cardName = xmlParser.nextText();
                        Log.v(LOGGING_TAG, "Added name: " + cardName);
                        break;
                    case "cost":
                        cardCost = Integer.parseInt(xmlParser.nextText());
                        Log.v(LOGGING_TAG, "Added cost: " + cardCost);
                        break;
                    case "card_type":
                        cardType = getCardTypeFromString(xmlParser.nextText());
                        Log.v(LOGGING_TAG, "Added type: " + cardType);
                        break;
                    case "faction":
                        faction = getFactionFromString(xmlParser.nextText());
                        Log.v(LOGGING_TAG, "Added faction: " + faction);
                        break;
                    case "ability":
                        Effect ability = getCardAbility(xmlParser);
                        cardEffects.add(ability);
                        Log.v(LOGGING_TAG, "Added ability: " + ability);
                        break;
                    case "synergy":
                        Effect synergyAbility = getCardAbility(xmlParser);
                        cardSynergyEffects.add(synergyAbility);
                        Log.v(LOGGING_TAG, "Added synergy ability: " + synergyAbility);
                        break;
                    case "isGuard":
                        isGuard = Boolean.parseBoolean(xmlParser.nextText());
                        Log.v(LOGGING_TAG, "Added isGuard: " + isGuard);
                        break;
                    case "health":
                        health = Integer.parseInt(xmlParser.nextText());
                        Log.v(LOGGING_TAG, "Added health: " + health);
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognized Card-XML tag.");
                }
            }
            if (event == XmlPullParser.END_TAG) break;
        }
        if (isChampion)
            return new Champion(
                    cardName,
                    cardCost,
                    cardType,
                    faction,
                    cardEffects,
                    cardSynergyEffects,
                    isGuard,
                    health);
        else
            return new Card(cardName, cardCost, cardType, faction, cardEffects, cardSynergyEffects);
    }

    private static CardType getCardTypeFromString(String nextText) {
        switch (nextText) {
            case "champion":
                return CardType.CHAMPION;
            case "action":
                return CardType.ACTION;
            case "item":
                return CardType.ITEM;
            default:
                throw new IllegalArgumentException("Unrecognized CardType: " + nextText);
        }
    }

    private static Faction getFactionFromString(String s) {
        switch (s) {
            case "guild":
                return Faction.GUILD;
            case "imperial":
                return Faction.IMPERIAL;
            case "necros":
                return Faction.NECROS;
            case "wild":
                return Faction.WILD;
            case "none":
                return Faction.NONE;
            default:
                throw new IllegalArgumentException("Unrecognized card type \"" + s + "+\".");
        }
    }

    private static Effect getCardAbility(XmlPullParser xmlParser)
            throws XmlPullParserException, IOException {
        Effect cardEffect = null;
        Log.v(LOGGING_TAG, "Start working on ability.");
        int event = 0;
        String name;

        int amount = -1;
        while (event != XmlPullParser.END_DOCUMENT) {
            event = xmlParser.next();
            name = xmlParser.getName();
            if (event == XmlPullParser.START_TAG) {
                switch (name) {
                    case "type":
                        cardEffect = parseEffect(xmlParser.nextText(), amount);
                        Log.v(LOGGING_TAG, "Added ability " + cardEffect);
                        break;
                    case "amount":
                        amount = Integer.parseInt(xmlParser.nextText());
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unrecognized Ability-XML tag \"" + name + "\"");
                }
            }
            if (event == XmlPullParser.END_TAG) break;
        }
        return cardEffect;
    }

    private static Effect parseEffect(String nextText, int amount) {
        if (amount == -1)
            throw new IllegalArgumentException(
                    "Ability Ordering Error: The \"amount\"-tag must come before"
                            + " the \"type\"-tag.");
        switch (nextText) {
            case "coin":
                return new CoinEffect(amount);
            case "attack":
                return new DamageEffect(amount);
            case "heal":
                return new HealingEffect(amount);
            case "draw":
                return new DrawEffect(amount);
            case "damagePerGuardInPlay":
                return new DamagePerGuardInPlayEffect(amount);
            case "damagePerChampionInPlay":
                return new DamagePerChampionInPlayEffect(amount);
            case "healingPerChampionInPlay":
                return new HealingPerChampionInPlayEffect(amount);
            default:
                throw new IllegalArgumentException(
                        "Ability type \"" + nextText + "\" not recognized");
        }
    }
}
