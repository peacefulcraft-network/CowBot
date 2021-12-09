package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;
import net.peacefulcraft.cowbot.translation.DiscordToMinecraftFormattingTranslator;

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

    DiscordToMinecraftFormattingTranslator translator = new DiscordToMinecraftFormattingTranslator(content);
    formattedComponents = translator.translate(formattedComponents);

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
}