package net.peacefulcraft.cowbot.consumers;

import java.util.function.Consumer;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import net.peacefulcraft.cowbot.Cow;
import net.peacefulcraft.cowbot.CowBot;

public class DisconnectEventHandler implements Consumer<DisconnectEvent> {

  private Cow bot;

  public DisconnectEventHandler(Cow bot) {
    this.bot = bot;
  }

  @Override
  public void accept(DisconnectEvent t) {
    if (bot.isActive()) {
      CowBot.logMessage("Disconnected from Discord Gateway");
    } else {
      CowBot.logError("Connection to Discord Gateway lost.");
    }
  }
}