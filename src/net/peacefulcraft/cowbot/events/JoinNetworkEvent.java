package net.peacefulcraft.cowbot.events;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;

public class JoinNetworkEvent implements Listener{
  @EventHandler
  public void joinGameEvent(PostLoginEvent event) {
    if (CowBot.getConfig().getGamechatChannelId() == null || CowBot.getConfig().getGamechatChannelId().length() < 1) { return; }
    
    ProxiedPlayer p = event.getPlayer();
    CowBot.logMessage("Player " + p.getName() + " permission fetch result " + p.hasPermission("multichat.staff.silentjoin"));
    if (p.hasPermission("multichat.staff.silentjoin")) { return; }

    String message = "*" + event.getPlayer().getName() + " joined the network*";
    CowBot.runAsync(
      () -> {
        Snowflake gamechatChannelId = Snowflake.of(CowBot.getConfig().getGamechatChannelId());
        CowBot.getCow().getGatewayConnection().getChannelById(gamechatChannelId)
          .ofType(TextChannel.class)
          .flatMap(channel -> channel.createMessage(message))
          .block();
      }
    );
  }
}