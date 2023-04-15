package at.vunfer.openrealms.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;

public class DeckGeneratorTest {

    @Test
    public void testGenerateDeckFromXml() {
        String xmlToParse = "<deck><card amount=\"3\"><name>testName</name><cost>5</cost><ability><amount>5</amount><type>coin</type></ability></card><card amount=\"1\"><name>otherTestName</name><cost>16</cost><ability><amount>2</amount><type>attack</type></ability><ability><amount>5</amount><type>heal</type></ability></card></deck>";
        XmlPullParser xml = getXmlFromString(xmlToParse);

        Deck<Card> expectedDeck = new Deck<>();
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(new Card("testName", 5, new ArrayList<>(List.of(new CoinEffect(5)))));
        expectedDeck.add(new Card("otherTestName", 16, new ArrayList<>(List.of(new DamageEffect(2), new HealingEffect(5)))));

        Deck<Card> deck = DeckGenerator.generateDeckFromXml(xml);
        assertEquals(expectedDeck, deck);
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
