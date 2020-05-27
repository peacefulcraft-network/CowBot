package net.peacefulcraft.cowbot.translation;

public class RankTranslatior {
  public static String serverRankToDiscordEmoji(String rank) {
    if (rank.equalsIgnoreCase("admin")) {
      return "<:admin:710929543942242350>";
    } else if (rank.equalsIgnoreCase("moderator")) {
      return "<:moderator:710933704045756517>";
    } else if (rank.equalsIgnoreCase("assistant")) {
      return "<:assistant:710933723394080834>";
    } else {
      return "<:user:710935326155079801>";
    }
  }
}