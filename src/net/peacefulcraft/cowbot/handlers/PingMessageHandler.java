package net.peacefulcraft.cowbot.handlers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class PingMessageHandler {

  public static Mono<Message> handle(Message arg) {
    return arg.getChannel().block().createMessage("Why you gotta be like that");
  }
}