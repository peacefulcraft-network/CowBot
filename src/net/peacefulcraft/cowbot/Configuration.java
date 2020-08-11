package net.peacefulcraft.cowbot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Configuration {

  private net.md_5.bungee.config.Configuration config;

  public Configuration() {
    createDefaultConfiguration();
    loadConfiguration();
  }

  public void onDisable() {}

  /**
   * Creates the default configuration file if one does not already exist.
   */
  private void createDefaultConfiguration() {
    try {
      File configYml = new File(CowBot.getInstance().getDataFolder(), "config.yml");
      if (!configYml.exists()) {
        configYml.getParentFile().mkdir();
        InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml");
        Files.copy(in, configYml.toPath());
      } else {
        CowBot.logMessage("Found existing config.yml - not creating a new one");
      }
    } catch (Exception e) {
      CowBot.logError("Unable to initialize the default configuration file. Please create it by hand.");
      e.printStackTrace();
    }
  }

  /**
   * Load the configuration file
   */
  private void loadConfiguration() {
    try {
      config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(CowBot.getInstance().getDataFolder(), "config.yml"));
    } catch (IOException e) {
      CowBot.logError("Unable to read configuration file. Does it exist? Is there a YAMl syntax error?");
      e.printStackTrace();
    }
  }

  /**
   * Save configuration values to disk
   */
  private void saveConfiguration() {
    try {
      ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,
          new File(CowBot.getInstance().getDataFolder(), "config.yml"));
    } catch (IOException e) {
      CowBot.logError("Unable to save configuration file.");
      e.printStackTrace();
    }
  }

  public String getBotStatus() { return config.getString("bot_stats"); }
  public void setBotStatus(String status) { config.set("bot_status", status); saveConfiguration(); }

  String getBotToken() { return config.getString("bot_token"); }
  void setBotToken(String token) { config.set("bot_token", token); saveConfiguration();}

  public String getGamechatChannelId() { return config.getString("gamechat_channel_id"); }
  public void setGamechatChannelId(String id) { config.set("gamechat_channel_id", id); saveConfiguration(); }

  public String getStaffchatChannelId() { return config.getString("staffchat_channel_id"); }
  public void setStaffchatChannelId(String id) { config.set("staffchat_channel_id", id); saveConfiguration(); }

  public String getCCChannelId() { return config.getString("cc_channel_id"); }
  public void setCCChannelId(String id) { config.set("cc_channel_id", id); saveConfiguration(); }

  protected String getDatabaseHost() { return config.getString("db_host"); }
  protected String getDatabaseUser() { return config.getString("db_user"); }
  protected String getDatabasePassword() { return config.getString("db_password"); }
  protected String getDatabaseName() { return config.getString("db_name"); }
}