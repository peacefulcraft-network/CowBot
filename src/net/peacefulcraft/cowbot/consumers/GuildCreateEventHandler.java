package net.peacefulcraft.cowbot.consumers;

import java.util.function.Consumer;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import net.peacefulcraft.cowbot.Cow;
import net.peacefulcraft.cowbot.CowBot;

public class GuildCreateEventHandler implements Consumer<GuildCreateEvent> {

  private Cow bot;

  public GuildCreateEventHandler(Cow bot) {
    this.bot = bot;
  }

  @Override
  public void accept(GuildCreateEvent event) {
    CowBot.logMessage("Connected to Discord Guild " + event.getGuild().getName());
  }
}