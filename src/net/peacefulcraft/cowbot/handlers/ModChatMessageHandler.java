package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Message;
import xyz.olivermartin.multichat.bungee.StaffChatManager;

public class ModChatMessageHandler {

  public static void accept(Message message) {
    if (!message.getAuthor().isPresent()) { return; }
    String author = message.getAuthor().get().getUsername();;

    if (!message.getContent().isPresent()) { return; }
    String content = message.getContent().get();

    (new StaffChatManager()).sendModMessage(author, author, "Discord", content);
  }
}