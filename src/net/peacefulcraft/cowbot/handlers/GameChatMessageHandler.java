package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;

public class GameChatMessageHandler {

  public static void handle(Message message) {
    if (!message.getAuthor().isPresent()) { return; }
    String author = message.getAuthor().get().getUsername();;

    if (!message.getContent().isPresent()) { return; }
    String content = message.getContent().get();

    BaseComponent[] formattedMessage = new ComponentBuilder()
      .append("[").color(ChatColor.GREEN)
      .append("Discord").color(ChatColor.GOLD)
      .append("]").color(ChatColor.GREEN)
      .append(author + ": ").color(ChatColor.GRAY)
      .append(content).color(ChatColor.WHITE).create();

    CowBot.logMessage(
      ChatColor.GREEN + " [" +
      ChatColor.GOLD + "Discord" +
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