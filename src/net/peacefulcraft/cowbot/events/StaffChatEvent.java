package net.peacefulcraft.cowbot.events;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;
import net.peacefulcraft.cowbot.handlers.ModChatMessageHandler;
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
          String message = "**" + event.getSenderName() + "**: " + event.getRawMessage();
          Snowflake staffChannelId = Snowflake.of(CowBot.getConfig().getStaffchatChannelId());
          CowBot.getCow().getBot().getChannelById(staffChannelId)
            .ofType(TextChannel.class)
            .flatMap(channel -> channel.createMessage(message))
            .block();
        });
    }
  }
}