package net.peacefulcraft.cowbot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

abstract public class Database {
  public static boolean isMinecraftAccountLinked(ProxiedPlayer player) throws SQLException
    { return isMinecraftAccountLinked(player.getUniqueId()); }
  public static boolean isMinecraftAccountLinked(UUID uuid) throws SQLException{
    Connection con = null;
    try {
      con = CowBot.getHikariPool().getConnection();
      PreparedStatement stmt = con.prepareStatement("SELECT `link` FROM `discordsrv_accounts` WHERE `uuid`=?");
      stmt.setString(1, uuid.toString());
      ResultSet res = stmt.executeQuery();

      return res.next();
    } catch (SQLException e) {
      e.printStackTrace();
      CowBot.logError("Error executing isMinecraftAccountLinked database call.");
      throw e;

    } finally {
      try {
        if (con != null) { con.close(); }
      } catch (SQLException e) {
        e.printStackTrace();
        CowBot.logError("Error releasing resources from isMinecraftAccountLinked database call.");
      }
    }
  }

  public static String createLinkCode(ProxiedPlayer player) throws SQLException
    { return createLinkCode(player.getUniqueId()); }
  public static String createLinkCode(UUID uuid) throws SQLException {
    Connection con = null;
    try {
      Random random = new Random();
      String linkCode = random.ints(97, 122).limit(5)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
      Long expiration = System.currentTimeMillis() + 300000;

      con = CowBot.getHikariPool().getConnection();
      PreparedStatement stmt = con.prepareStatement("INSERT INTO `discordsrv_codes` VALUES(?,?,?)");
      stmt.setString(1, linkCode);
      stmt.setString(2, uuid.toString());
      stmt.setLong(3, expiration);
      int res = stmt.executeUpdate();
      return linkCode;

    } catch(SQLException e) {
      e.printStackTrace();
      CowBot.logError("Error creating link code in database");
      throw e;

    } finally {
      try {
        if (con != null) { con.close(); }
      } catch(SQLException e) {
        e.printStackTrace();
        CowBot.logError("Error releasing resources from createLinkCode database call.");
      }
    }
  }

  public static boolean useLinkCode(String discordId, String code) throws SQLException {
    Connection con = null;
    try {
      con = CowBot.getHikariPool().getConnection();

      PreparedStatement stmt = con.prepareStatement("SELECT `uuid`, `expiration` FROM `discordsrv_codes` WHERE `code`=?");
      stmt.setString(1, code);
      ResultSet resSelect = stmt.executeQuery();

      if (!resSelect.next()) {
        return false;
      }

      String uuid = resSelect.getString(1);
      Long expiration = resSelect.getLong(2);

      if (expiration < System.currentTimeMillis()) {
        return false;
      }

      stmt = con.prepareStatement("INSERT INTO `discordsrv_codes`(discord, uuid) VALUES (?,?)");
      stmt.setString(1, discordId);
      stmt.setString(2, uuid);
      int resInsert = stmt.executeUpdate();
      if (resInsert == 1) {
        stmt = con.prepareStatement("DELETE FROM `discordsrv_codes` WHERE `code`=?");
        stmt.setString(1, code);
        stmt.executeUpdate();
        return true;
      } else { return false; }
    } catch(SQLException e) {
      e.printStackTrace();
      CowBot.logError("Error linking Discord account.");
      throw e;
    } finally {
      try {
        if (con != null) { con.close(); }
      } catch (SQLException e) {
        e.printStackTrace();
        CowBot.logError("Error releasing resources from useLinkCode call");
      }
    }
  }

  public static boolean unlinkAccount(ProxiedPlayer player) throws SQLException
    { return unlinkAccount(player.getUniqueId()); }
  public static boolean unlinkAccount(UUID uuid) throws SQLException {
    Connection con = null;
    try {
      con = CowBot.getHikariPool().getConnection();
      PreparedStatement stmt = con.prepareStatement("DELETE FROM `discordsrv_accounts` WHERE `uuid`=?");
      stmt.setString(1, uuid.toString());
      int res = stmt.executeUpdate();
      if (res == 0) { return false; }
      return true;

    } catch(SQLException e) {
      e.printStackTrace();
      CowBot.logError("Error unlinking Discord account.");
      throw e;

    } finally {
      try {
        if (con != null) { con.close(); }
      } catch (SQLException e) {
        e.printStackTrace();
        CowBot.logError("Error releasing resources from unlinkAccount call.");
      }
    }
  }

  public static void removeExpiredLinkCodes() throws SQLException {
    Connection con = null;
    try {
      con = CowBot.getHikariPool().getConnection();
      PreparedStatement stmt = con.prepareStatement("DELETE FROM `discordsrv_codes` WHERE `expiration` < ?");
      stmt.setLong(1, System.currentTimeMillis());
      stmt.executeUpdate();
    } catch(SQLException e) {
      e.printStackTrace();
      CowBot.logError("Error on removeExpiredLinkCodes call");
      throw e;
    } finally {
      try {
        if (con != null) { con.close(); }
      } catch (SQLException e) {
        e.printStackTrace();
        CowBot.logError("Error releasing resources from removeExpiredLinkCodes call.");
      }
    }
  }
}