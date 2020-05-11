package net.peacefulcraft.cowbot;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.peacefulcraft.cowbot.commands.CowBotCommand;

public class CowBot extends Plugin{

  public static CowBot instance;
    public static CowBot getInstance() { return instance; }

  public static String getPluginPrefix() { return ChatColor.GREEN + "[" + ChatColor.BLUE + "Cow" + ChatColor.GREEN + "]" + ChatColor.RESET; }

  private static Configuration config;
    public static Configuration getConfig() { return config; }

  private static Logger logger;

  private static Cow cow;
    private static Thread botThread;
    public static Cow getCow() { return cow; }

  public CowBot() {
    instance = this;
    logger = getLogger();

    getProxy().getPluginManager().registerCommand(this, new CowBotCommand());
  }

  public void onEnable() {
    config = new Configuration();
    if (config.getBotToken().length() > 0) {
      this.cow =  new Cow(config.getBotToken());
      botThread = new Thread(this.cow, "CowBot - Discord Bot");
      botThread.start();
    } else {
      logWarning("No bot token found in config.yml. Add one and then run /cb reload");
    }
  }

  public void reload() {
    onDisable();
    onEnable();
  }

  public void onDisable() {
    if (cow != null) { cow.onDisable(); }
    if (config != null) { config.onDisable(); }
    logMessage("CowBot Disabled");
  }

  public static BaseComponent generateTextComponent(String message) {
    return new TextComponent(getPluginPrefix() + ChatColor.GRAY + message);
  }

  public static void logMessage(String message) {
    logger.log(Level.INFO, getPluginPrefix() + message);
  }

  public static void logWarning(String message) {
    logger.log(Level.WARNING, getPluginPrefix() + message);
  }

  public static void logError(String message) {
    logger.log(Level.SEVERE, getPluginPrefix() + message);
  }
}