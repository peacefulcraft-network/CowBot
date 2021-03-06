package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;
import net.peacefulcraft.cowbot.translation.ColorTranslator;

public class GameChatMessageHandler {

  public static void handle(Message message) {
    if (!message.getAuthor().isPresent()) { return; }
    String author = message.getAuthor().get().getUsername();;

    Member sender = message.getAuthor().get().asMember(message.getGuild().block().getId()).block();
    ChatColor senderColor = ColorTranslator.ColorToColorCode(sender.getColor().block());
    String senderRank = sender.getRoles().blockLast().getName();

    if (!message.getContent().isPresent()) { return; }
    String content = message.getContent().get();


    BaseComponent[] formattedMessage = new ComponentBuilder()
      .append("[").color(ChatColor.GREEN)
      .append("Discord").color(ChatColor.GOLD)
      .append("][").color(ChatColor.GREEN)
      .append(senderRank).color(senderColor)
      .append("]").color(ChatColor.GREEN)
      .append(author + ": ").color(ChatColor.GRAY)
      .append(content).color(ChatColor.WHITE).create();

    CowBot.logMessage(
      ChatColor.GREEN + " [" +
      ChatColor.GOLD + "Discord" +
      ChatColor.GREEN + "][" +
      senderColor + senderRank +
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
}