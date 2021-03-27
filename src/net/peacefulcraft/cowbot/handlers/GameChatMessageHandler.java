package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;

public class GameChatMessageHandler {

  public static void handle(Message message) {
    String author = message.getAuthor().get().getUsername();
    Member sender = message.getAuthor().get().asMember(message.getGuild().block().getId()).block();
    Color color = sender.getColor().block();
    String roleHex = String.format("#%02X%02X%02X", color.getRed(), color.getBlue(), color.getGreen());
    
    String senderRank = sender.getHighestRole().block().getName();
    String content = message.getContent();

    BaseComponent[] formattedMessage = new ComponentBuilder()
      .append("[").color(ChatColor.GREEN)
      .append("Discord").color(ChatColor.GOLD)
      .append("][").color(ChatColor.GREEN)
      .append(senderRank).color(ChatColor.of(roleHex))
      .append("]").color(ChatColor.GREEN)
      .append(author + ": ").color(ChatColor.GRAY)
      .append(content).color(ChatColor.WHITE).create();

      CowBot.logMessage(
        ChatColor.GREEN + " [" +
        ChatColor.GOLD + "Discord" +
        ChatColor.GREEN + "][" +
        ChatColor.of(roleHex) + senderRank +
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