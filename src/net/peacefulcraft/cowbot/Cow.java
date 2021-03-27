package net.peacefulcraft.cowbot;

import java.util.ArrayList;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.peacefulcraft.cowbot.handlers.DisconnectEventHandler;
import net.peacefulcraft.cowbot.handlers.GameChatMessageHandler;
import net.peacefulcraft.cowbot.handlers.GuildCreateEventHandler;
import net.peacefulcraft.cowbot.handlers.ListCommandHandler;
import net.peacefulcraft.cowbot.handlers.ModChatMessageHandler;
import net.peacefulcraft.cowbot.handlers.PingMessageHandler;
import net.peacefulcraft.cowbot.handlers.ReadyEventHandler;

public class Cow implements Runnable{

  private GatewayDiscordClient gateway;
    public GatewayDiscordClient getGatewayConnection() { return gateway; }

  private String token;

  private boolean active = true;
    public boolean isActive() { return active; }

  private ArrayList<String> subscribedChannels;

  public Cow(String token) {
    this.token = token;
  }

  public static boolean isCommand(String content) {
    if (content.length() > 1) {
      if (content.charAt(0) == '!') {
        return true;
      }
    }
    return false;
  }

  @Override
  public void run() {
    gateway = DiscordClient.create(token).login().block();
    gateway.updatePresence(Presence.online(Activity.listening("your conversations")));

    subscribedChannels = new ArrayList<String>();
    subscribedChannels.add(CowBot.getConfig().getCCChannelId());
    subscribedChannels.add(CowBot.getConfig().getGamechatChannelId());
    subscribedChannels.add(CowBot.getConfig().getStaffchatChannelId());

    // Setup stream consumers
    gateway.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(ReadyEventHandler::handle);

    gateway.getEventDispatcher().on(GuildCreateEvent.class)
      .subscribe(GuildCreateEventHandler::handle);

    gateway.getEventDispatcher().on(DisconnectEvent.class)
      .subscribe(DisconnectEventHandler::handle);

    gateway.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> 
        message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getCCChannelId())
        || message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getStaffchatChannelId())
      )
      .filter(message -> message.getType() == Message.Type.DEFAULT)
      .filter(message -> message.getContent().equalsIgnoreCase("!poke"))
      .map(PingMessageHandler::handle)
      .subscribe();

    gateway.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getType() == Message.Type.DEFAULT)
      .filter(message -> message.getContent().equalsIgnoreCase("!list"))
      .subscribe(ListCommandHandler::handle);

    gateway.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getGamechatChannelId()))
      .filter(message -> !message.getAuthor().get().isBot())
      .filter(message -> message.getType() == Message.Type.DEFAULT)
      .filter(message -> !isCommand(message.getContent()))
      .subscribe(GameChatMessageHandler::handle);

    gateway.getEventDispatcher().on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getChannelId().asString().equalsIgnoreCase(CowBot.getConfig().getStaffchatChannelId()))
      .filter(message -> !message.getAuthor().get().isBot())
      .filter(message -> message.getType() == Message.Type.DEFAULT)
      .filter(message -> !isCommand(message.getContent()))
      .subscribe(ModChatMessageHandler::handle);

    CowBot.logMessage("Obtaining thread lock");
    gateway.onDisconnect().block();
    CowBot.logMessage("Releasing thread lock");
  }

  public void onDisable() {
    active = false;
    gateway.logout().block();
  }
}