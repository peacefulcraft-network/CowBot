package net.peacefulcraft.cowbot.handlers;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import net.peacefulcraft.cowbot.CowBot;

public class ReadyEventHandler {
  public static void handle(ReadyEvent event) {
    User self = event.getSelf();
    CowBot.logMessage("Connected to Discord Gatway as " + self.getUsername());
  }
}