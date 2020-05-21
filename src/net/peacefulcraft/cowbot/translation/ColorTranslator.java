package net.peacefulcraft.cowbot.translation;

import java.awt.Color;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

public class ColorTranslator {
  private static final HashMap<String, ChatColor> colorMap = new HashMap<String, ChatColor>() {{
    put("99AAB5", ChatColor.WHITE);
    put("1ABC9C", ChatColor.GREEN);
    put("2ECC71", ChatColor.GREEN);
    put("3498DB", ChatColor.BLUE);
    put("9B59B6", ChatColor.DARK_PURPLE);
    put("E91E63", ChatColor.LIGHT_PURPLE);
    put("F1C40F", ChatColor.YELLOW);
    put("E67E22", ChatColor.GOLD);
    put("E74C3C", ChatColor.RED);
    put("95A5A6", ChatColor.GRAY);
    put("607D8B", ChatColor.DARK_GRAY);
    put("11806A", ChatColor.DARK_GREEN);
    put("1F8B4C", ChatColor.DARK_GREEN);
    put("206694", ChatColor.DARK_BLUE);
    put("71368A", ChatColor.DARK_PURPLE);
    put("AD1457", ChatColor.LIGHT_PURPLE);
    put("C27C0E", ChatColor.GOLD);
    put("A84300", ChatColor.GOLD);
    put("992D22", ChatColor.DARK_RED);
    put("bb1515", ChatColor.DARK_RED);
    put("979C9F", ChatColor.GRAY);
    put("546E7A", ChatColor.DARK_GRAY);
  }};

  public static ChatColor ColorToColorCode(Color color) {
    String hex = Integer.toHexString(color.getRGB());
    if (colorMap.containsKey(hex)) {
      return colorMap.get(hex);
    } else {
      return ChatColor.LIGHT_PURPLE;
    }
  }
}