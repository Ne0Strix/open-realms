/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DeckGeneratorTest {

    @Test
    public void testGeneratePlayerStarterDeck() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Deck<Card> generatedDeck = DeckGenerator.generatePlayerStarterDeck(targetContext);

        Deck<Card> manuallyGeneratedDeck =
                DeckGenerator.generateDeckFromXml(
                        targetContext.getResources().getXml(R.xml.player_starter_deck));

        assertEquals(manuallyGeneratedDeck, generatedDeck);
    }

    @Test
    public void testGenerateMarketDeck() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Deck<Card> generatedDeck = DeckGenerator.generateMarketDeck(targetContext);

        Deck<Card> manuallyGeneratedDeck =
                DeckGenerator.generateDeckFromXml(
                        targetContext.getResources().getXml(R.xml.market_deck));

        assertEquals(manuallyGeneratedDeck, generatedDeck);
    }

    @Test
    public void testGenerateDeckFromXml() {
        String xmlToParse =
                "<deck>"
                        + "<card amount=\"3\">"
                        + "   <name>testName</name>"
                        + "   <cost>5</cost>"
                        + "   <ability>"
                        + "      <amount>5</amount>"
                        + "      <type>coin</type>"
                        + "   </ability>"
                        + "</card>"
                        + "<card amount=\"1\">"
                        + "   <name>otherTestName</name>"
                        + "   <cost>16</cost>"
                        + "   <ability>"
                        + "       <amount>2</amount>"
                        + "       <type>attack</type>"
                        + "   </ability>"
                        + "   <ability>"
                        + "       <amount>5</amount>"
                        + "       <type>heal</type>"
                        + "   </ability>"
                        + "</card>"
                        + "</deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        Deck<Card> expectedDeck = new Deck<>();
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(
                new Card(
                        "otherTestName",
                        16,
                        new ArrayList<>(List.of(new DamageEffect(2), new HealingEffect(5)))));

        Deck<Card> deck = DeckGenerator.generateDeckFromXml(xml);

        assertEquals(expectedDeck, deck);
    }

    @Test
    public void testInvalidXmlStructure() {
        String xmlToParse = "This is not an Xml";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        assertThrows(
                "Unspecified XmlPullParserException: ",
                IllegalArgumentException.class,
                () -> DeckGenerator.generateDeckFromXml(xml));
    }

    @Test
    public void testInvalidTag() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <cardName>Gold</cardName>"
                        + "        <cardCost>0</cardCost>"
                        + "        <cardAbility>"
                        + "            <amount>1</amount>"
                        + "            <type>coin</type>"
                        + "        </cardAbility>"
                        + "    </card>"
                        + "</deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        assertThrows(
                "Unrecognized Card-XML tag.",
                IllegalArgumentException.class,
                () -> DeckGenerator.generateDeckFromXml(xml));
    }

    @Test
    public void testFileEndedWhileParsingCard() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <name>Gold</name>"
                        + "        <cost>0</cost>"
                        + "        <ability>"
                        + "            <amount>1</amount>"
                        + "            <type>coin</type>"
                        + "        </ability>";
        XmlPullParser xml = getXmlFromString(xmlToParse);
        Deck<Card> expectedDeck = new Deck<>();
        expectedDeck.add(new Card("Gold", 0, List.of(new CoinEffect(1))));

        Deck<Card> resultDeck = DeckGenerator.generateDeckFromXml(xml);

        assertEquals(expectedDeck, resultDeck);
    }

    @Test
    public void testFileEndedWhileParsingAbility() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <name>Gold</name>"
                        + "        <cost>0</cost>"
                        + "        <ability>"
                        + "            <amount>1</amount>"
                        + "            <type>coin</type>";
        XmlPullParser xml = getXmlFromString(xmlToParse);
        Deck<Card> expectedDeck = new Deck<>();
        expectedDeck.add(new Card("Gold", 0, List.of(new CoinEffect(1))));

        Deck<Card> resultDeck = DeckGenerator.generateDeckFromXml(xml);

        assertEquals(expectedDeck, resultDeck);
    }

    @Test
    public void testUnexpectedAbilityTag() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <name>Gold</name>"
                        + "        <cost>0</cost>"
                        + "        <ability>"
                        + "            <amount>1</amount>"
                        + "            <other>wrongTag</other>"
                        + "            <type>coin</type>"
                        + "        </ability>"
                        + "    </card>"
                        + "</deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        assertThrows(
                "Unrecognized Ability-XML tag.",
                IllegalArgumentException.class,
                () -> DeckGenerator.generateDeckFromXml(xml));
    }

    @Test
    public void testInvalidAbility() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <name>Gold</name>"
                        + "        <cost>0</cost>"
                        + "        <ability>"
                        + "            <amount>1</amount>"
                        + "            <type>not a real ability</type>"
                        + "        </ability>"
                        + "    </card>"
                        + "</deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        assertThrows(
                "Ability type not recognized",
                IllegalArgumentException.class,
                () -> DeckGenerator.generateDeckFromXml(xml));
    }

    @Test
    public void testInvalidAbilityTagOrder() {
        String xmlToParse =
                "<deck>"
                        + "    <card amount=\"1\">"
                        + "        <name>Gold</name>"
                        + "        <cost>0</cost>"
                        + "        <ability>"
                        + "            <type>attack</type>"
                        + "            <amount>10</amount>"
                        + "        </ability>"
                        + "    </card>"
                        + "</deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        assertThrows(
                "Ability Ordering Error: The \"amount\"-tag must come before the \"type\"-tag.",
                IllegalArgumentException.class,
                () -> DeckGenerator.generateDeckFromXml(xml));
    }

    public XmlPullParser getXmlFromString(String xml) {
        XmlPullParser parser = null;
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new ByteArrayInputStream(xml.getBytes()), null);
            return parser;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return parser;
    }
}
