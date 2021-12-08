package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameChatMessageHandler {

  public static void handle(Message message) {
    String author = message.getAuthor().get().getUsername();
    Member sender = message.getAuthor().get().asMember(message.getGuild().block().getId()).block();
    Color color = sender.getColor().block();

    // fully qualified name to avoid name collisions with discord4j Color
    java.awt.Color rankColor = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    
    String senderRank = sender.getHighestRole().block().getName();
    String content = message.getContent();

    ComponentBuilder formattedComponents = new ComponentBuilder()
      .append("[").color(ChatColor.GREEN)
      .append("Discord").color(ChatColor.GOLD)
      .append("][").color(ChatColor.GREEN)
      .append(senderRank).color(ChatColor.of(rankColor))
      .append("]").color(ChatColor.GREEN)
      .append(author + ": ").color(ChatColor.GRAY);
      // .append(content).color(ChatColor.WHITE);
    formattedComponents = formatDiscordMessageForMinecraft(formattedComponents, content);

    BaseComponent[] formattedMessage = formattedComponents.color(ChatColor.WHITE).create();

      CowBot.logMessage(
        ChatColor.GREEN + " [" +
        ChatColor.GOLD + "Discord" +
        ChatColor.GREEN + "][" +
        ChatColor.of(rankColor) + senderRank +
        ChatColor.GREEN + "]" + 
        ChatColor.GRAY + author + ": " + 
        ChatColor.WHITE + content
      );

    for(ProxiedPlayer player : CowBot.getInstance().getProxy().getPlayers()) {
      if (player != null) {
        player.sendMessage(formattedMessage);
      }
    }
  }

  /*
   * A simple Discord formatting parser which converts to Minecraft formatting. 
   * Doesn't look like there's a better way without using a library written in another language, or one which would do
   * heavy-duty parsing of the entire Markdown language, which we don't want. For example, we want <b>test</b> to be passed
   * along as plaintext. Formats character by character, which is easier to implement.
   * 
   * Only parses one layer deep -- will only check for the following:
   * https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-
   * 
   * Keys:
   * italics: * or _
   * bold: **
   * underline: __
   * strikethrough: ~~
   * obfuscated: ||
   * escape key: \ (can escape itself)
   */
  private static class FormatToPos {
    public FormatToPos(int f, int p) {
      fmt = f;
      pos = p;
    }
    int fmt;
    int pos;
  }
  private static boolean shouldAddFormatting(int fmt, int i, List<Stack<FormatToPos>> formats) {
    // only add if greater than or equal to start of range
    boolean should = !formats.get(fmt).empty() && i >= formats.get(fmt).peek().fmt;
    if (should) {
      CowBot.logMessage("APPLYING " + fmt + " TO INDEX " + i);
    }
    return should;
  }
  private static ComponentBuilder formatDiscordMessageForMinecraft(ComponentBuilder formattedPrefix, String content) {
    CowBot.logMessage("BEGINNING PARSING");
    int ITALICS_UND = 0;
    int ITALICS_AST = 1;
    int BOLD = 2;
    int UNDERLINE = 3;
    int STRIKETHROUGH = 4;
    int OBFUSCATED = 5;
    String[] types = {"ITALICS_UND", "ITALICS_AST", "BOLD", "UNDERLINE", "STRIKETHROUGH", "OBFUSCATED" };

    // -1 means closed, otherwise the value is the pos at which this was opened
    int NOT_OPEN = content.length()+1; // so open values always compare as "more recent"
    int[] isOpen = {NOT_OPEN, NOT_OPEN, NOT_OPEN, NOT_OPEN, NOT_OPEN, NOT_OPEN};

    // whether or not this character should be ignored in the output (formatting char)
    boolean[] ignore = new boolean[content.length()];

    // stack of observed formatting characters
    Stack<FormatToPos> stack = new Stack<FormatToPos>();

    List<Stack<FormatToPos>> formats = new ArrayList<Stack<FormatToPos>>(6);
    for (int i = 0; i < 6; ++i) {
      formats.add(new Stack<FormatToPos>());
    }
    // compile time hash table which stores which formatting codes are currently "open" (basically vector of bool isOpen)
    // stack which stores which formatting codes are open, but in order of which we expect to close first
    // basic alg: 
    // if see formatting code:
    //    if not already open (check hash table): open it and add it to stack (with position in string)
    //    else: close it, popping off anything which was in its way on the stack (returning those to their previous positions in the string)
    //      then, remember that string of formatting (push into relevant queue)
    // else: do nothing

    // result is 5 queues (one for each formatting type) with start, end pairs.
    // then go through each char, check which types of formatting it should have, and append it to the formatted message
    for (int i = content.length()-1; i >= 0; --i) {
      char curr = content.charAt(i);
      char prev = i == 0 ? ' ' : content.charAt(i-1); // todo mj account for corner cases with mulitple *s, multiple _s
      int format = -1;
      if (prev == '\\' && (curr == '*' || curr == '_' || curr == '~' || curr == '|')) {
        // ignore escape char and continue
        ignore[i-1] = true;
        continue;
      }

      if (curr == '*') {
        format = ITALICS_AST;

        // prefer whichever formatting code is closer to the top of the stack
        // but break ties in favor of the 'stronger' formatting (bold, here)
        if (prev == '*' && isOpen[ITALICS_AST] >= isOpen[BOLD]) {
          format = BOLD;
        }
      } else if (curr == '_') {
        format = ITALICS_UND;

        // same as above, prefer whichever is closer to top of stack
        if (prev == '_' && isOpen[ITALICS_UND] >= isOpen[UNDERLINE]) {
          format = UNDERLINE;
        }
      } else if (curr == '~' && prev == '~') {
        format = STRIKETHROUGH;
      } else if (curr == '|' && prev == '|') {
        format = OBFUSCATED;
      } else {
        // nothing, continue
        continue;
      }

      CowBot.logMessage(types[format] + " DETECTED");

      // set the formatting chars to be ignored in the string
      ignore[i] = true;
      if (format != ITALICS_AST && format != ITALICS_UND) { // todo mj make hash table of format configs so this is extensible? can just check "is_double"
        // two character codes get double ignores
        ignore[i-1] = true;
        // decrement index twice to prevent double counting the formatting
        --i;
      }

      // determine if this is an opening reference or a closing one
      if (isOpen[format] != NOT_OPEN) {
        // close the reference, popping any which are on "top" of it in the process
        while (stack.peek().fmt != format) {
          isOpen[stack.peek().fmt] = NOT_OPEN;

          // un-ignore the keys
          ignore[stack.peek().pos] = false;
          if (stack.peek().fmt != ITALICS_AST && stack.peek().fmt != ITALICS_UND) {
            ignore[stack.peek().pos+1] = false;
          }
          stack.pop();
        }
        
        // ref is now closed! note that we have an open, close pair
        isOpen[format] = NOT_OPEN;
        // i is openpos, remembered pos is closepos
        formats.get(format).push(new FormatToPos(i, stack.peek().pos));
        stack.pop();
      } else {
        // opening reference, save it
        isOpen[format] = i;
        stack.push(new FormatToPos(format, i));
      }
    }
    CowBot.logMessage("DONE WITH PARSING");
    
    // whatever is open still never got closed, must pop off and un-ignore
    while(!stack.empty()) {
      ignore[stack.peek().pos] = false;
      if (stack.peek().fmt != ITALICS_AST && stack.peek().fmt != ITALICS_UND) {
        ignore[stack.peek().pos+1] = false;
      }
      stack.pop();
    }

    // iterate over pairs in formats, set chars accordingly
    CowBot.logMessage("MESSAGE PARSING DEBUG LOG");
    CowBot.logMessage("content: " + content);
    String ignoreStr = "";
    for (int i = 0; i < content.length(); ++i) {
      ignoreStr += (ignore[i] ? "T" : "F");
    }
    CowBot.logMessage("ignores: " + ignoreStr);
    String fmtString = "";
    for (int i = 0; i < 6; i++) {
      if (formats.get(i).empty()) {
        continue;
      }
      CowBot.logMessage(types[i] + " FOUND:");
      for (int j = 0; j < formats.get(i).size(); ++j) {
        CowBot.logMessage("\t(" + formats.get(i).get(j).fmt + "," + formats.get(i).get(j).pos + ")");
      }
    }
    CowBot.logMessage("FORMATTING: " + fmtString);

    // now iterate through the chars and add them with their proper formatting to the builder
    for (int i = 0; i < content.length(); ++i) {
      // first pop all used-up formatters
      for (int f = 0; f < formats.size(); ++f) {
        while (!formats.get(f).empty() && formats.get(f).peek().pos <= i) {
          formats.get(f).pop();
        }
      }

      // ignore if not adding
      if (ignore[i]) {
        continue;
      }

      // add char with all formatting, then reset
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
}