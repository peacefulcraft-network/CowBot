package net.peacefulcraft.cowbot.handlers;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peacefulcraft.cowbot.CowBot;

public class ListCommandHandler {
  public static void handle(Message message) {
    Set<String> servers = CowBot.getInstance().getProxy().getServers().keySet();
    HashMap<String, ArrayList<String>> counts = new HashMap<String, ArrayList<String>>();
    servers.forEach(server -> { counts.put(server, new ArrayList<String>()); });

    for(ProxiedPlayer player : CowBot.getInstance().getProxy().getPlayers()) {
      counts.get(player.getServer().getInfo().getName()).add(player.getName());
    }
    CowBot.runAsync(
      () -> {
        message.getChannel().block().createMessage(messageSpec -> messageSpec.setEmbed(embed -> {
          embed.setColor(Color.of(169, 220, 169));
          embed.setAuthor("PeacefulCraft Network Status", "https://status.peacefulcraft.net", "https://www.peacefulcraft.net/assets/logo-aglqhi2l.png");
          
          long graphEnd = ZonedDateTime.now().toInstant().toEpochMilli() - 60000;
          long graphStart = graphEnd - 3600000;
          // embed.setImage("https://status.peacefulcraft.net/render/d-solo/MSFaPWjZz/peacefulcraft-network-player-counts?orgId=1&panelId=4&from=" + graphStart + "&to=" + graphEnd);

          embed.setTimestamp(Instant.now());
          embed.setFooter("Network Player Counts", "");
      
          for(Entry<String, ArrayList<String>> list : counts.entrySet()) {
            String userList = "**(" + list.getValue().size() + ")**";
            for(String username : list.getValue()) {
              userList += " " + username + ",";
            }

            if (list.getValue().size() > 0){
              userList = userList.substring(0, userList.length() - 1);
            }

            embed.addField(list.getKey(), userList, false);
          }
        })).block();
      }
    );
  }
}