package net.peacefulcraft.cowbot.handlers;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import net.peacefulcraft.cowbot.CowBot;

public class GuildCreateEventHandler {
  public static void handle(GuildCreateEvent event) {
    CowBot.logMessage("Connected to Discord Gateway");
  }
}