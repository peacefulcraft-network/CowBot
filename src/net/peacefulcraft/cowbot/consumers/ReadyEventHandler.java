package net.peacefulcraft.cowbot.consumers;

import java.util.function.Consumer;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import net.peacefulcraft.cowbot.Cow;
import net.peacefulcraft.cowbot.CowBot;

public class ReadyEventHandler implements Consumer<ReadyEvent> {

  private Cow bot;

  public ReadyEventHandler(Cow bot) {
    this.bot = bot;
  }

  @Override
  public void accept(ReadyEvent event) {
    User self = event.getSelf();
    CowBot.logMessage("Connected to Discord Gatway as " + self.getUsername());
  }
}