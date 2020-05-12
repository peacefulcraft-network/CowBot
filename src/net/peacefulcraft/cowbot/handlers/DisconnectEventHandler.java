package net.peacefulcraft.cowbot.handlers;

import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import net.peacefulcraft.cowbot.CowBot;

public class DisconnectEventHandler {
  public static void handle(DisconnectEvent t) {
    if (CowBot.getCow().isActive()) {
      CowBot.logError("Connection to Discord Gateway lost.");
    } else {
      CowBot.logMessage("Gracefully Disconnected from Discord Gateway");
    }
  }
}