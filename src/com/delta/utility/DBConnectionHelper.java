package com.delta.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.delta.constant.GenericConstant;

public class DBConnectionHelper {
	private static final Logger LOGGER = Logger
			.getLogger(DBConnectionHelper.class);
	private static DBConnectionHelper instance;
	private Properties dbProperties;
	private BasicDataSource dataSource;

	private DBConnectionHelper() {
		FileInputStream file = null;
		String DB_DRIVER_CLASS = null;
		String DB_URL = null;
		String DB_USERNAME = null;
		String DB_PASSWORD = null;
		try {
			this.dbProperties = new Properties();
			// load the file handle for main.properties
			file = new FileInputStream("db.properties");
			// load all the properties from this file
			this.dbProperties.load(file);
			// we have loaded the properties, so close the file handle
			PropertyReaderHelper propertyReaderHelper = PropertyReaderHelper
					.getInstancePropertyReaderHelper();
			// Data Type MySQL/Oracle/
			if (GenericConstant.MYSQL_DB_TYPE.equals(propertyReaderHelper
					.getValue("SQL_DB_TYPE"))) {
				DB_DRIVER_CLASS = this.dbProperties
						.getProperty("MYSQL_DB_DRIVER_CLASS");
				DB_URL = this.dbProperties.getProperty("MYSQL_DB_URL");
				DB_USERNAME = this.dbProperties
						.getProperty("MYSQL_DB_USERNAME");
				DB_PASSWORD = this.dbProperties
						.getProperty("MYSQL_DB_PASSWORD");
			} else if (GenericConstant.ORACLE_DB_TYPE
					.equals(propertyReaderHelper.getValue("SQL_DB_TYPE"))) {
				DB_DRIVER_CLASS = this.dbProperties
						.getProperty("ORACLE_DB_DRIVER_CLASS");
				DB_URL = this.dbProperties.getProperty("ORACLE_DB_URL");
				DB_USERNAME = this.dbProperties
						.getProperty("ORACLE_DB_USERNAME");
				DB_PASSWORD = this.dbProperties
						.getProperty("ORACLE_DB_PASSWORD");
			}
			this.dataSource = new BasicDataSource();
			this.dataSource.setDriverClassName(DB_DRIVER_CLASS);
			this.dataSource.setUrl(DB_URL);
			this.dataSource.setUsername(DB_USERNAME);
			this.dataSource.setPassword(DB_PASSWORD);
			this.dataSource.setMaxActive(100);
			this.dataSource.setMaxWait(10000);
			dataSource.setMaxIdle(10);
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("FileNotFoundException" + fnfe.getMessage());
		} catch (IOException ioe) {
			LOGGER.error("FileNotFoundException" + ioe.getMessage());
		} finally {
			try {
				if (null != file) {
					file.close();
				}
			} catch (IOException ioe) {
				LOGGER.error("Could not close" + ioe.getMessage());
			}
		}

	}

	// thread safe double locking mode of lazy initialization
	public static DBConnectionHelper getInstanceDBConnectionHelper() {
		if (instance == null) {
			synchronized (DBConnectionHelper.class) {
				if (instance == null) {
					instance = new DBConnectionHelper();
				}
			}
		}
		return instance;
	}
	//returns Connection object
	public Connection getConnection() throws SQLException{
		return this.dataSource.getConnection();
	}
}
