package net.peacefulcraft.cowbot.consumers;

import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import net.peacefulcraft.cowbot.Cow;
import net.peacefulcraft.cowbot.CowBot;

public class CommandControlMessageHandler implements Consumer<MessageCreateEvent> {

  private Cow bot;

  public CommandControlMessageHandler(Cow bot) {
    this.bot = bot;
  }

  @Override
  public void accept(MessageCreateEvent event) {
    Message message = event.getMessage();
    CowBot.logMessage("Saw message " + message.getAuthor().get() + " " + message.getContent().get());

    if (message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getCCChannelId())) { return; }
    message.getChannel().map(channel -> channel.createMessage("Don't touch me"));
  }
}