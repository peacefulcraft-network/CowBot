package net.peacefulcraft.cowbot.events;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peacefulcraft.cowbot.CowBot;
import xyz.olivermartin.multichat.bungee.events.PostStaffChatEvent;

public class StaffChatEvent implements Listener {
  @EventHandler
  public void staffChatEvent(PostStaffChatEvent event) {
    CowBot.logMessage("Got staff chat");
    if (event.getType() == "mod") {
      CowBot.logMessage("Got mod chat");
      if (CowBot.getConfig().getStaffchatChannelId() == null || CowBot.getConfig().getStaffchatChannelId().length() < 1) { return; }

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