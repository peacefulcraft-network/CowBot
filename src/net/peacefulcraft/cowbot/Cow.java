package net.peacefulcraft.cowbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.peacefulcraft.cowbot.handlers.DisconnectEventHandler;
import net.peacefulcraft.cowbot.handlers.GuildCreateEventHandler;
import net.peacefulcraft.cowbot.handlers.ModChatMessageHandler;
import net.peacefulcraft.cowbot.handlers.PingMessageHandler;
import net.peacefulcraft.cowbot.handlers.ReadyEventHandler;

public class Cow implements Runnable{
  private static DiscordClient bot;
    public DiscordClient getBot() { return bot; }

  private boolean active = true;
    public boolean isActive() { return active; }

  public Cow(String token) {
    bot = DiscordClientBuilder.create(token).build();
    bot.updatePresence(Presence.online(Activity.listening("your conversations")));

    // Setup stream consumers
    bot.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(ReadyEventHandler::handle);

    bot.getEventDispatcher().on(GuildCreateEvent.class)
      .subscribe(GuildCreateEventHandler::handle);

    bot.getEventDispatcher().on(DisconnectEvent.class)
      .subscribe(DisconnectEventHandler::handle);

    bot.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getCCChannelId()))
      .filter(message -> message.getContent().orElse("").equalsIgnoreCase("!poke"))
      .flatMap(PingMessageHandler::handle)
      .subscribe();

    bot.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getStaffchatChannelId()))
      .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
      .subscribe(ModChatMessageHandler::accept);
  }

  @Override
  public void run() {
    CowBot.logMessage("Obtaining thread lock");
    bot.login().block();
    CowBot.logMessage("Releasing thread lock");
  }

  public void onDisable() {
    active = false;
    if (bot.isConnected()) {
      bot.logout();
    }
    bot = null;
  }
}