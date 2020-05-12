package net.peacefulcraft.cowbot.events;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;

public class JoinNetworkEvent implements Listener{
  @EventHandler
  public void joinGameEvent(PostLoginEvent event) {
    if (CowBot.getConfig().getGamechatChannelId() == null || CowBot.getConfig().getGamechatChannelId().length() < 1) { return; }

    String message = "*" + event.getPlayer().getName() + " joined the network*";
    CowBot.runAsync(
      () -> {
        Snowflake gamechatChannelId = Snowflake.of(CowBot.getConfig().getGamechatChannelId());
        CowBot.getCow().getBot().getChannelById(gamechatChannelId)
          .ofType(TextChannel.class)
          .flatMap(channel -> channel.createMessage(message))
          .block();
      }
    );
  }
}