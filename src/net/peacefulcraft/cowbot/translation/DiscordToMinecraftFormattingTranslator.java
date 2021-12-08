package net.peacefulcraft.cowbot.translation;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.peacefulcraft.cowbot.CowBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
* A simple Discord formatting parser which converts to Minecraft formatting.
* 
* Will only check for the following:
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

  /* PUBLIC MEMBER FUNCTIONS */
  public DiscordToMinecraftFormattingTranslator(String content_) {
      content = content_;

      // sentinel "not open" value set higher, so open values always compare as "more recent"
      NOT_OPEN = content.length()+1;
      ignore = new boolean[content.length()];
      for (int i = 0; i < NUM_FORMATS; ++i) {
          isOpen[i] = NOT_OPEN;
          formats.add(new Stack<Range>());
      }
  }

  public ComponentBuilder parse() {
    ComponentBuilder cb = new ComponentBuilder();
    return parse(cb);
  }

  public ComponentBuilder parse(ComponentBuilder formattedPrefix) {

    // stack of observed formatting characters
    Stack<OpenFormat> stack = new Stack<OpenFormat>();
  
    // todo mj try iterating in forward order
  
    for (int i = content.length()-1; i >= 0; --i) {
    // for (int i = 0; i < content.length(); ++i) {
      char curr = content.charAt(i);
      char prev = i == 0 ? ' ' : content.charAt(i-1);
      char prevprev = i > 1 ? content.charAt(i-2) : ' ';
      int format = -1;

    //   // handle escape char first
    //   if (prev == '\\' && (curr == '*' || curr == '_' || curr == '~' || curr == '|' || curr == '\\')) {
    //     ignore[i-1] = true;
    //     --i;
    //     continue;
    //   }
    //   // also applies to all 'double char' formatting codes
    //   if (prevprev == '\\' && ((curr == '~' && prev == '~') || (curr == '_' && prev == '_') || (curr == '|' && prev == '|') || (curr == '*' && prev == '*'))) {
    //     ignore[i-2] = true;
    //     i-=2;
    //     continue;
    //   }

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
        CowBot.logMessage("ESCAPED CHAR: " + (format != ESCAPE_CHAR ? types[format] : "ESCAPE_CHAR"));
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
        // close the reference, popping any which are on "top" of it in the process
        while (stack.peek().format != format) {
          isOpen[stack.peek().format] = NOT_OPEN;

          // un-ignore the keys
          ignore[stack.peek().openedAtPos] = false;
          if (!isSingleCharFormatType(stack.peek().format)) {
            ignore[stack.peek().openedAtPos+1] = false;
          }
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

    // whatever is open still never got closed, must pop off and un-ignore
    while(!stack.empty()) {
      ignore[stack.peek().openedAtPos] = false;
      if (!isSingleCharFormatType(stack.peek().format)) {
        ignore[stack.peek().openedAtPos+1] = false;
      }
      stack.pop();
    }
    
    // for debugging purposes
    logParsingData(ignore, formats);

    // now iterate through the chars and add them with their proper formatting to the builder
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

  /* PRIVATE HELPERS */
  private class OpenFormat {
    public OpenFormat(int f, int p) {
      format = f;
      openedAtPos = p;
    }
    int format;
    int openedAtPos;
  }

  private class Range {
      public Range(int s, int e) {
          start = s;
          end = e;
      }
      int start;
      int end;
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

  private void logParsingData(boolean[] ignore, List<Stack<Range>> formats) {
    CowBot.logMessage("MESSAGE PARSING DEBUG LOG");
    CowBot.logMessage("content: " + content);
    String ignoreStr = "";
    for (int i = 0; i < content.length(); ++i) {
      ignoreStr += (ignore[i] ? "T" : "F");
    }
    CowBot.logMessage("ignores: " + ignoreStr);
    String fmtString = "";
    for (int i = 0; i < NUM_FORMATS; i++) {
      if (formats.get(i).empty()) {
        continue;
      }
      CowBot.logMessage(types[i] + " FOUND:");
      for (int j = 0; j < formats.get(i).size(); ++j) {
        CowBot.logMessage("\t(" + formats.get(i).get(j).start + "," + formats.get(i).get(j).end + ")");
      }
    }
    CowBot.logMessage("FORMATTING: " + fmtString);
  }
}
