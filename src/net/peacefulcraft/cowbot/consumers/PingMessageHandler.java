package net.peacefulcraft.cowbot.consumers;

import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class PingMessageHandler implements Consumer<MessageCreateEvent> {

  @Override
  public void accept(MessageCreateEvent event) {
    event.getMessage().getContent().ifPresent(content -> {
      if (content.equalsIgnoreCase("!poke")) {
        event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Don't touch me"));
      }
    });
  }
  
}