package net.peacefulcraft.cowbot.translation;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.peacefulcraft.cowbot.CowBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
* A simple Discord formatting parser which converts to Minecraft formatting.
* Class is split up to allow better unit testing.
* Does not match true Discord formatting for complex patterns.
* 
* Supports the following formatting features:
* https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-
* 
* Keys:
* italics: * or _
* bold: **
* underline: __
* strikethrough: ~~
* obfuscated: ||
* escape key: \ (can escape itself)
* 
*/
public class DiscordToMinecraftFormattingTranslator {

  /* STATIC CONSTANTS */
  private static int NUM_FORMATS = 6;

  private static int ITALICS_UND = 0;
  private static int ITALICS_AST = 1;
  private static int BOLD = 2;
  private static int UNDERLINE = 3;
  private static int STRIKETHROUGH = 4;
  private static int OBFUSCATED = 5;
  private static String[] types = {"ITALICS_UND", "ITALICS_AST", "BOLD", "UNDERLINE", "STRIKETHROUGH", "OBFUSCATED" };
  
  private static int ESCAPE_CHAR = -1;

  /* DATA MEMBERS */
  private String content;
  private int NOT_OPEN;
  private int[] isOpen = new int[NUM_FORMATS];
  private List<Stack<Range>> formats = new ArrayList<Stack<Range>>(NUM_FORMATS);
  private boolean[] ignore;

  // simple [start, end] range POD type
  public static class Range {
    public Range(int s, int e) {
        start = s;
        end = e;
    }
    public int start;
    public int end;
  }

  /* PUBLIC MEMBERS */
  public DiscordToMinecraftFormattingTranslator(String content_) {
      content = content_;

      // sentinel "not open" value set higher, so open values always compare as "more recent"
      NOT_OPEN = content.length()+1;
      ignore = new boolean[content.length()];
      for (int i = 0; i < NUM_FORMATS; ++i) {
          isOpen[i] = NOT_OPEN;
          formats.add(new Stack<Range>());
      }
      parse();
      // logParsingData();
  }

  // used by unit tests
  public Stack<Range> getItalicsAst() {
    return formats.get(ITALICS_AST);
  }
  public Stack<Range> getItalicsUnd() {
    return formats.get(ITALICS_UND);
  }
  public Stack<Range> getBold() {
    return formats.get(BOLD);
  }
  public Stack<Range> getUnderlines() {
    return formats.get(UNDERLINE);
  }
  public Stack<Range> getStrikethroughs() {
    return formats.get(STRIKETHROUGH);
  }
  public Stack<Range> getObfuscated() {
    return formats.get(OBFUSCATED);
  }

  public void logParsingData() {
    log("MESSAGE PARSING DEBUG LOG");
    log("content: " + content);
    String ignoreStr = "";
    for (int i = 0; i < content.length(); ++i) {
      ignoreStr += (ignore[i] ? "T" : "F");
    }
    log("ignores: " + ignoreStr);
    String fmtString = "";
    for (int i = 0; i < NUM_FORMATS; i++) {
      if (formats.get(i).empty()) {
        continue;
      }
      log(types[i] + " FOUND:");
      for (int j = 0; j < formats.get(i).size(); ++j) {
        log("\t(" + formats.get(i).get(j).start + "," + formats.get(i).get(j).end + ")");
      }
    }
    log("FORMATTING: " + fmtString);
  }

  // with data structures initialized from a previous parse, translate
  // the parsed components to minecraft based formatting
  public ComponentBuilder translate() {
    ComponentBuilder cb = new ComponentBuilder();
    return translate(cb);
  }
  public ComponentBuilder translate(ComponentBuilder formattedPrefix) {
    // iterate through the chars and add them with their proper formatting to the builder
    for (int i = 0; i < content.length(); ++i) {
      // first pop all used-up formatters
      for (int f = 0; f < formats.size(); ++f) {
        while (!formats.get(f).empty() && formats.get(f).peek().end <= i) {
          formats.get(f).pop();
        }
      }

      // ignore if not adding
      if (ignore[i]) {
        continue;
      }

      // reset formatting then add current char with all types
      formattedPrefix.append("").reset();
      formattedPrefix.append("" + content.charAt(i));
      formattedPrefix.italic(shouldAddFormatting(ITALICS_AST, i, formats) || shouldAddFormatting(ITALICS_UND, i, formats));
      formattedPrefix.bold(shouldAddFormatting(BOLD, i, formats));
      formattedPrefix.underlined(shouldAddFormatting(UNDERLINE, i, formats));
      formattedPrefix.strikethrough(shouldAddFormatting(STRIKETHROUGH, i, formats));
      formattedPrefix.obfuscated(shouldAddFormatting(OBFUSCATED, i, formats));
    }

    return formattedPrefix;
  }

  /* PRIVATE MEMBERS */
  private class OpenFormat {
    public OpenFormat(int f, int p) {
      format = f;
      openedAtPos = p;
    }
    int format;
    int openedAtPos;
  }

  private void parse() {
    // stack of observed formatting characters
    Stack<OpenFormat> stack = new Stack<OpenFormat>();
  
    for (int i = content.length()-1; i >= 0; --i) {
      char curr = content.charAt(i);
      char prev = i == 0 ? ' ' : content.charAt(i-1);
      char prevprev = i > 1 ? content.charAt(i-2) : ' ';
      int format = -1;

      if (curr == '*') {
        format = ITALICS_AST;

        // prefer whichever formatting code is closer to the top of the stack
        // but break ties in favor of the 'stronger' formatting (bold, here)
        if (prev == '*' && isOpen[ITALICS_AST] >= isOpen[BOLD]) {
          format = BOLD;
        }
      } else if (curr == '_') {
        format = ITALICS_UND;

        // same as above, break ties in favor of underline
        if (prev == '_' && isOpen[ITALICS_UND] >= isOpen[UNDERLINE]) {
          format = UNDERLINE;
        }
      } else if (curr == '~' && prev == '~') {
        format = STRIKETHROUGH;
      } else if (curr == '|' && prev == '|') {
        format = OBFUSCATED;
      } else if (curr == '\\') {
        format = ESCAPE_CHAR; 
      } else {
        // nothing, continue
        continue;
      }

      // if the code is escaped, continue, ignoring the escape char
      if (isEscaped(format, curr, prev, prevprev)) {
        if (isSingleCharFormatType(format)) {
            i--;
        } else {
            i-=2;
        }
        ignore[i] = true;
        continue;
      }

      // if there's an un-escaped escape char (just a backslash), treat that as a normal character
      if (format == ESCAPE_CHAR) {
        continue;
      }

      // set the formatting chars to be ignored in the string
      ignore[i] = true;
      if (!isSingleCharFormatType(format)) {
        // two character codes get double ignores
        ignore[i-1] = true;
        // decrement index twice to prevent double counting the formatting
        --i;
      }

      // determine if this is an opening reference or a closing one
      if (isOpen[format] != NOT_OPEN) {
        // close the reference, popping and restoring any which are on "top" of it in the process
        while (stack.peek().format != format) {
          isOpen[stack.peek().format] = NOT_OPEN;
          restoreUnclosedFormat(stack.peek());
          stack.pop();
        }

        // ref is now closed! note that we have an open, close pair
        formats.get(format).push(new Range(i, stack.peek().openedAtPos));
        isOpen[format] = NOT_OPEN;
        stack.pop();

      } else {
        // opening reference, save it
        isOpen[format] = i;
        stack.push(new OpenFormat(format, i));
      }
    }

    // restore all formats which were never closed
    while(!stack.empty()) {
      restoreUnclosedFormat(stack.peek());
      stack.pop();
    }
  }

  private void restoreUnclosedFormat(OpenFormat open) {
    ignore[open.openedAtPos] = false;
    if (!isSingleCharFormatType(open.format)) {
      // need to un-ignore next character if double
      ignore[open.openedAtPos+1] = false;
    }
  }

  private boolean isEscaped(int format, char curr, char prev, char prevprev) {
    // if single char format type, just check prev, else check prevprev
    if (isSingleCharFormatType(format)) {
        return prev == '\\';
    }
    return prevprev == '\\';
  }

  private boolean isSingleCharFormatType(int format) {
    return format == ITALICS_AST || format == ITALICS_UND || format == ESCAPE_CHAR;
  }

  private boolean shouldAddFormatting(int fmt, int i, List<Stack<Range>> formats) {
    // only add if greater than or equal to start of range
    return !formats.get(fmt).empty() && i >= formats.get(fmt).peek().start;
  }

  private void log(String message) {
    if (CowBot.getInstance() != null) {
      String prefix = ChatColor.GREEN + "[" + ChatColor.AQUA + "TRANSLATOR" + ChatColor.GREEN + "]" + ChatColor.RESET;
      CowBot.logMessage(prefix + message);
    }
  }
}