package net.peacefulcraft.cowbot;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.peacefulcraft.cowbot.commands.CowBotCommand;
import net.peacefulcraft.cowbot.events.GameChatEvent;
import net.peacefulcraft.cowbot.events.JoinNetworkEvent;
import net.peacefulcraft.cowbot.events.LeaveNetworkEvent;
import net.peacefulcraft.cowbot.events.StaffChatEvent;

public class CowBot extends Plugin {

  public static String getPluginPrefix() {
    return ChatColor.GREEN + "[" + ChatColor.BLUE + "Cow" + ChatColor.GREEN + "]" + ChatColor.RESET;
  }

  public static CowBot instance;
    public static CowBot getInstance() { return instance; }

  private static Configuration config;
    public static Configuration getConfig() { return config; }

  private static Logger logger;
  private static Cow cow;
    public static Cow getCow() { return cow; }
  private static Thread botThread;
  private static HikariManager dbPool;
    public static HikariManager getHikariPool() { return dbPool; }

  private StaffChatEvent staffChatEventHandler;
  private GameChatEvent gameChatEventHandler;
  private JoinNetworkEvent joinNetworkEventHandler;
  private LeaveNetworkEvent leaveNetworkEventHandler;

  public CowBot() {
    instance = this;
    logger = getLogger();

    getProxy().getPluginManager().registerCommand(this, new CowBotCommand());
  }

  public void onEnable() {
    config = new Configuration();
    if (config.getDatabaseName().length() > 0) {
      dbPool = new HikariManager(config);
      if (!dbPool.isAlive()) {
        logError("Unable to establish connections to MySQL server. Profile links will not work.");
      }
    }

    if (config.getBotToken().length() > 0) {
      cow = new Cow(config.getBotToken());
      botThread = new Thread(cow, "CowBot - Discord Bot");
      botThread.start();

      logMessage("Gamechat channelId: " + config.getGamechatChannelId());

      if(config.getGamechatChannelId() != null && config.getGamechatChannelId().length() > 0) {
        logMessage("Registering gamechat listners");
        gameChatEventHandler = new GameChatEvent();
        getProxy().getPluginManager().registerListener(this, gameChatEventHandler);

        joinNetworkEventHandler = new JoinNetworkEvent();
        getProxy().getPluginManager().registerListener(this, joinNetworkEventHandler);

        leaveNetworkEventHandler = new LeaveNetworkEvent();
        getProxy().getPluginManager().registerListener(this, leaveNetworkEventHandler);
      } else {
        logWarning("No gamechat_channel_id found in config.yml. Add one and then run /cb reload");
      }

      if (config.getStaffchatChannelId() != null && config.getStaffchatChannelId().length() > 0) {
        logMessage("Registering staff chat listeners");
        staffChatEventHandler = new StaffChatEvent();
        getProxy().getPluginManager().registerListener(this, staffChatEventHandler);
      } else {
        logWarning("No staffchat_channel_id found in config.yml. Add one and then run /cb reload.");
      }
    } else {
      logWarning("No bot token found in config.yml. Add one and then run /cb reload");
    }
  }

  public void reload() {
    onDisable();
    onEnable();
  }

  public void onDisable() {
    if (gameChatEventHandler != null) {
      getProxy().getPluginManager().unregisterListener(gameChatEventHandler);
      getProxy().getPluginManager().unregisterListener(joinNetworkEventHandler);
      getProxy().getPluginManager().unregisterListener(leaveNetworkEventHandler);
      gameChatEventHandler = null;
      joinNetworkEventHandler = null;
      leaveNetworkEventHandler = null;
    }

    if (staffChatEventHandler != null) {
      getProxy().getPluginManager().unregisterListener(staffChatEventHandler);
      staffChatEventHandler = null;
    }

    if (cow != null) { cow.onDisable(); }
    if (config != null) { config.onDisable(); }
    if (dbPool != null && dbPool.isAlive()) { dbPool.close(); }

    logMessage("CowBot Disabled");
  }

  // must strip message for "new" formatting characters which will slip through the legacy stripColor call. namely "&x"
  public static String stripAmpersandBasedAndLegacyColorCodes(String message) {
    return ChatColor.stripColor(message).replaceAll("&x", "");
  }

  public static void runAsync(Runnable task) {
    instance.getProxy().getScheduler().runAsync(instance, task);
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