package net.peacefulcraft.cowbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.peacefulcraft.cowbot.consumers.CommandControlMessageHandler;
import net.peacefulcraft.cowbot.consumers.DisconnectEventHandler;
import net.peacefulcraft.cowbot.consumers.GuildCreateEventHandler;
import net.peacefulcraft.cowbot.consumers.PingMessageHandler;
import net.peacefulcraft.cowbot.consumers.ReadyEventHandler;

public class Cow implements Runnable{
  private DiscordClient bot;
  private boolean active = true;
    public boolean isActive() { return active; }

  public Cow(String token) {
    bot = DiscordClientBuilder.create(token).build();
    bot.updatePresence(Presence.online(Activity.listening("your conversations")));

    // Setup stream consumers
    bot.getEventDispatcher().on(ReadyEvent.class).subscribe( new ReadyEventHandler(this) );
    bot.getEventDispatcher().on(GuildCreateEvent.class).subscribe( new GuildCreateEventHandler(this) );
    bot.getEventDispatcher().on(DisconnectEvent.class).subscribe( new DisconnectEventHandler(this) );
    bot.getEventDispatcher().on(MessageCreateEvent.class).subscribe( new CommandControlMessageHandler(this) );
    bot.getEventDispatcher().on(MessageCreateEvent.class).subscribe( new PingMessageHandler() );
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