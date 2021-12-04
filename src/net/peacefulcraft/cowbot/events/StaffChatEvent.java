package net.peacefulcraft.cowbot.events;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;
import net.peacefulcraft.cowbot.handlers.ModChatMessageHandler;
import net.peacefulcraft.cowbot.translation.RankTranslatior;
import xyz.olivermartin.multichat.bungee.events.PostStaffChatEvent;

public class StaffChatEvent implements Listener {
  @EventHandler
  public void staffChatEvent(PostStaffChatEvent event) {
    if (event.getType() == "mod") {
      if (CowBot.getConfig().getStaffchatChannelId() == null || CowBot.getConfig().getStaffchatChannelId().length() < 1) { return; }
      
      // Don't echo back messages from events the bot generated
      if (ModChatMessageHandler.isReplayMessage(event.getSenderName(), event.getRawMessage())) { return; }

      CowBot.runAsync(
        () -> {
          event.getSender().getGroups().forEach((group) -> CowBot.logMessage(group));
          String rank = RankTranslatior.serverRankToDiscordEmoji(event.getSender().getGroups().iterator().next());
          String message = rank + " **" + CowBot.stripAmpersandBasedAndLegacyColorCodes(event.getSenderName() + "**: " + event.getRawMessage());
          Snowflake staffChannelId = Snowflake.of(CowBot.getConfig().getStaffchatChannelId());
          CowBot.getCow().getGatewayConnection().getChannelById(staffChannelId)
            .ofType(TextChannel.class)
            .flatMap(channel -> channel.createMessage(message))
            .block();
        });
    }
  }
}