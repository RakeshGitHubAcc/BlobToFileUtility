package com.btf.run;

import org.apache.log4j.Logger;

import com.delta.service.BlobToFileDBOImpl;
import com.delta.service.IBlobToFileDBO;
import com.delta.utility.PropertyReaderHelper;

public class MessageBoardDbMigrationSchd {
	private static final Logger LOGGER = Logger
			.getLogger(MessageBoardDbMigrationSchd.class);

	public static void main(String[] args) {
		PropertyReaderHelper propertyReaderHelper = PropertyReaderHelper
				.getInstancePropertyReaderHelper();
		Long startTime = System.currentTimeMillis();
		LOGGER.info("**************************************************");
		LOGGER.info("************Message Board DBMigrationSchd starts************");
		LOGGER.info("**************************************************");
		LOGGER.info("Scheduler is running in "
				+ propertyReaderHelper.getValue("APP.DEPLOYMENT.LEVEL")
				+ " mode - OFFSHORE ="
				+ propertyReaderHelper.getValue("OFFSHORE"));
		
		IBlobToFileDBO iBlobToFileDBO = new BlobToFileDBOImpl();
		// Blob to local temp file
		iBlobToFileDBO.MsgBoardBolbToTempDir(propertyReaderHelper
				.getValue("SQL_DB_TYPE"));
		// local file to sftp
		iBlobToFileDBO.writeTempDirFileToSFTP(
				propertyReaderHelper.getValue("sftp.local.temp.dir.msgbrd"),
				propertyReaderHelper.getValue("sftp.remoteHomeDirectory.msgbrd"));
		
		Long endTime = System.currentTimeMillis();
		LOGGER.info("Execution Time Taken:" + (endTime - startTime) / 1000
				+ "seconds");
		LOGGER.info("**************************************************");
		LOGGER.info("************Message Board DBMigrationSchd Ends************");
		LOGGER.info("**************************************************");
	}

}
