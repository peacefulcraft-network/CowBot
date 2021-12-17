package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Message;
import xyz.olivermartin.multichat.bungee.StaffChatManager;

public class ModChatMessageHandler {

  public static String lastUsernameRelayed = "";
  public static String lastMessageRelayed = "";
    public static boolean isReplayMessage(String username, String message) {
      return lastUsernameRelayed.equalsIgnoreCase(username) && lastMessageRelayed.equals(message);
    }

  public static void handle(Message message) {
    if (!message.getAuthor().isPresent()) { return; }
    String author = message.getAuthor().get().getUsername();
    String content = message.getContent();
    lastUsernameRelayed = author;
    lastMessageRelayed = content;

    (new StaffChatManager()).sendModMessage(author, author, "Discord", content);
  }
}