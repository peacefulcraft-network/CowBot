package test.java;

import net.peacefulcraft.cowbot.translation.DiscordToMinecraftFormattingTranslator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Stack;

public class TestDiscordToMinecraftFormattingTranslator {

    @Test
    public void testSimpleItalicsAst() {
        String message = "*i*";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getItalicsAst();
        assertEquals(0, result.peek().start);
        assertEquals(2, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsUnd().empty());
        assertTrue(translator.getBold().empty());
        assertTrue(translator.getUnderlines().empty());
        assertTrue(translator.getObfuscated().empty());
        assertTrue(translator.getStrikethroughs().empty());
    }

    @Test
    public void testSimpleItalicsUnd() {
        String message = "_i_";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getItalicsUnd();
        assertEquals(0, result.peek().start);
        assertEquals(2, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsAst().empty());
        assertTrue(translator.getBold().empty());
        assertTrue(translator.getUnderlines().empty());
        assertTrue(translator.getObfuscated().empty());
        assertTrue(translator.getStrikethroughs().empty());
    }

    @Test
    public void testSimpleBold() {
        String message = "**i**";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getBold();
        assertEquals(0, result.peek().start);
        assertEquals(3, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsAst().empty());
        assertTrue(translator.getItalicsUnd().empty());
        assertTrue(translator.getUnderlines().empty());
        assertTrue(translator.getObfuscated().empty());
        assertTrue(translator.getStrikethroughs().empty());
    }

    @Test
    public void testSimpleUnderline() {
        String message = "__i__";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getUnderlines();
        assertEquals(0, result.peek().start);
        assertEquals(3, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsAst().empty());
        assertTrue(translator.getBold().empty());
        assertTrue(translator.getItalicsUnd().empty());
        assertTrue(translator.getObfuscated().empty());
        assertTrue(translator.getStrikethroughs().empty());
    }

    @Test
    public void testSimpleObfuscated() {
        String message = "||i||";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getObfuscated();
        assertEquals(0, result.peek().start);
        assertEquals(3, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsAst().empty());
        assertTrue(translator.getBold().empty());
        assertTrue(translator.getItalicsUnd().empty());
        assertTrue(translator.getUnderlines().empty());
        assertTrue(translator.getStrikethroughs().empty());
    }

    @Test
    public void testSimpleStrikethrough() {
        String message = "~~i~~";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> result = translator.getStrikethroughs();
        assertEquals(0, result.peek().start);
        assertEquals(3, result.peek().end);
        result.pop();
        assertTrue(result.empty());
        assertTrue(translator.getItalicsAst().empty());
        assertTrue(translator.getBold().empty());
        assertTrue(translator.getItalicsUnd().empty());
        assertTrue(translator.getUnderlines().empty());
        assertTrue(translator.getObfuscated().empty());
    }

    @Test
    public void testSimpleBoldItalics() {
        String message = "***i***";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> bolds = translator.getBold();
        Stack<DiscordToMinecraftFormattingTranslator.Range> italics = translator.getItalicsAst();

        assertEquals(0, bolds.peek().start);
        assertEquals(5, bolds.peek().end);
        bolds.pop();
        assertTrue(bolds.empty());

        assertEquals(2, italics.peek().start);
        assertEquals(4, italics.peek().end);
        italics.pop();
        assertTrue(italics.empty());
    }

    @Test
    public void testSimpleUnderlineItalics() {
        String message = "___i___";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> unds = translator.getUnderlines();
        Stack<DiscordToMinecraftFormattingTranslator.Range> italics = translator.getItalicsUnd();

        assertEquals(0, unds.peek().start);
        assertEquals(5, unds.peek().end);
        unds.pop();
        assertTrue(unds.empty());

        assertEquals(2, italics.peek().start);
        assertEquals(4, italics.peek().end);
        italics.pop();
        assertTrue(italics.empty());
    }

    @Test
    public void testSimpleDisjoints() {
        String message = "___i___*j*";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> unds = translator.getUnderlines();
        Stack<DiscordToMinecraftFormattingTranslator.Range> italicsUnd = translator.getItalicsUnd();
        Stack<DiscordToMinecraftFormattingTranslator.Range> italicsAst = translator.getItalicsAst();

        assertEquals(0, unds.peek().start);
        assertEquals(5, unds.peek().end);
        unds.pop();
        assertTrue(unds.empty());

        assertEquals(2, italicsUnd.peek().start);
        assertEquals(4, italicsUnd.peek().end);
        italicsUnd.pop();
        assertTrue(italicsUnd.empty());

        assertEquals(7, italicsAst.peek().start);
        assertEquals(9, italicsAst.peek().end);
        italicsAst.pop();
        assertTrue(italicsAst.empty());
    }

    // @Test
    // public void testOverlappingItalicsBold1() {
    //     String message = "***b**i*";
    //     DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
    //     Stack<DiscordToMinecraftFormattingTranslator.Range> bold = translator.getBold();
    //     Stack<DiscordToMinecraftFormattingTranslator.Range> italicsAst = translator.getItalicsAst();

    //     assertEquals(1, bold.peek().start);
    //     assertEquals(4, bold.peek().end);
    //     bold.pop();
    //     assertTrue(bold.empty());

    //     assertEquals(0, italicsAst.peek().start);
    //     assertEquals(7, italicsAst.peek().end);
    //     italicsAst.pop();
    //     assertTrue(italicsAst.empty());
    // }

    @Test
    public void testOverlappingItalicsBold2() {
        String message = "***i*b**";
        DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(message);
        Stack<DiscordToMinecraftFormattingTranslator.Range> bold = translator.getBold();
        Stack<DiscordToMinecraftFormattingTranslator.Range> italicsAst = translator.getItalicsAst();

        assertEquals(0, bold.peek().start);
        assertEquals(6, bold.peek().end);
        bold.pop();
        assertTrue(bold.empty());

        assertEquals(2, italicsAst.peek().start);
        assertEquals(4, italicsAst.peek().end);
        italicsAst.pop();
        assertTrue(italicsAst.empty());
    }
}