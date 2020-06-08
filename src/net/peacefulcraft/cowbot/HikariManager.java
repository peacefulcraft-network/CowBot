package net.peacefulcraft.cowbot;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariManager {

	private HikariDataSource ds;
	
	public HikariManager(Configuration c) {
		HikariConfig hc = new HikariConfig();
		hc.setDriverClassName("org.mariadb.jdbc.Driver");
		hc.setJdbcUrl("jdbc:mariadb://" + c.getDatabaseHost() + ":3306/" + c.getDatabaseName());
		hc.setUsername(c.getDatabaseUser());
		hc.setPassword(c.getDatabasePassword());
		hc.setPoolName("CowBotData");
		/*
		 * TODO: Recomended optimizations
		 * https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
		 */
		
		ds = new HikariDataSource(hc);
	}
	
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	/**
	 * TOOD: Better health checking, logging, and error reporting
	 * @return
	 */
	public boolean isAlive() {
		return (ds.isRunning() && !ds.isClosed());
	}
	
	public void close() {
		ds.close();
	}
}