package net.peacefulcraft.cowbot.events;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;
import net.peacefulcraft.cowbot.translation.RankTranslatior;
import xyz.olivermartin.multichat.bungee.events.PostGlobalChatEvent;

public class GameChatEvent implements Listener {
  @EventHandler
  public void gameChatEvent(PostGlobalChatEvent event) {
    if (CowBot.getConfig().getGamechatChannelId() == null || CowBot.getConfig().getGamechatChannelId().length() < 1) { return; }

    CowBot.runAsync(
      () -> {
        String rank = RankTranslatior.serverRankToDiscordEmoji(
          LuckPermsProvider.get().getUserManager().getUser(event.getSender().getUniqueId()).getPrimaryGroup()
        );
        String message = rank + " **" + event.getRawSenderNickname() + "**: " + event.getRawMessage();
        Snowflake gamechatChannelId = Snowflake.of(CowBot.getConfig().getGamechatChannelId());
        CowBot.getCow().getBot().getChannelById(gamechatChannelId)
          .ofType(TextChannel.class)
          .flatMap(channel -> channel.createMessage(message))
          .block();
      }
    );
  }
}