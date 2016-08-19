package com.delta.utility;

import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.log4j.Logger;

public class SftpHelper {
	private static final Logger LOGGER = Logger.getLogger(SftpHelper.class);
	private static SftpHelper instance;
	private String SFTP_SERVER_ADDRESS;
	private String SFTP_USERNAME;
	private String SFTP_PASSWORD;
	private static StandardFileSystemManager manager;

	private SftpHelper() {
		PropertyReaderHelper propertyReaderHelper = PropertyReaderHelper
				.getInstancePropertyReaderHelper();
		this.SFTP_SERVER_ADDRESS = propertyReaderHelper.getValue(
				"sftp.serverAddress").trim();
		this.SFTP_USERNAME = propertyReaderHelper.getValue("sftp.userId")
				.trim();
		this.SFTP_PASSWORD = propertyReaderHelper.getValue("sftp.password")
				.trim();
		LOGGER.info("SFTP properties uploaded successfully.");
	}

	public static SftpHelper getInstanceSftpHelper() {
		if (null == instance) {
			synchronized (SftpHelper.class) {
				if (instance == null) {
					instance = new SftpHelper();
				}
			}
		}
		return instance;
	}

	// returns Connection object
	public StandardFileSystemManager getManager() {
		if (null == manager) {
			synchronized (SftpHelper.class) {
				if (manager == null) {
					manager = new StandardFileSystemManager();
				}
			}
		}
		return manager;
	}

	public String getSFTP_SERVER_ADDRESS() {
		return SFTP_SERVER_ADDRESS;
	}

	public void setSFTP_SERVER_ADDRESS(String sFTP_SERVER_ADDRESS) {
		SFTP_SERVER_ADDRESS = sFTP_SERVER_ADDRESS;
	}

	public String getSFTP_USERNAME() {
		return SFTP_USERNAME;
	}

	public void setSFTP_USERNAME(String sFTP_USERNAME) {
		SFTP_USERNAME = sFTP_USERNAME;
	}

	public String getSFTP_PASSWORD() {
		return SFTP_PASSWORD;
	}

	public void setSFTP_PASSWORD(String sFTP_PASSWORD) {
		SFTP_PASSWORD = sFTP_PASSWORD;
	}
}
