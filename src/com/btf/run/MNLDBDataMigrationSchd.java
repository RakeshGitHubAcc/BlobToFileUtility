package com.btf.run;

import org.apache.log4j.Logger;

import com.delta.service.BlobToFileDBOImpl;
import com.delta.service.IBlobToFileDBO;
import com.delta.utility.PropertyReaderHelper;

public class MNLDBDataMigrationSchd {
	private static final Logger LOGGER = Logger
			.getLogger(MNLDBDataMigrationSchd.class);

	public static void main(String[] args) {
		PropertyReaderHelper propertyReaderHelper = PropertyReaderHelper
				.getInstancePropertyReaderHelper();
		Long startTime = System.currentTimeMillis();
		LOGGER.info("**************************************************");
		LOGGER.info("************Manual Loader DB DataMigrationSchd starts************");
		LOGGER.info("**************************************************");
		LOGGER.info("Scheduler is running in "
				+ propertyReaderHelper.getValue("APP.DEPLOYMENT.LEVEL")
				+ " mode - OFFSHORE ="
				+ propertyReaderHelper.getValue("OFFSHORE"));

		// System.out.println("Testing...." + System.getProperty("user.dir"));
		IBlobToFileDBO iBlobToFileDBO = new BlobToFileDBOImpl();
		// Blob to local file
		iBlobToFileDBO.MNLBolbToTempDir(propertyReaderHelper
				.getValue("SQL_DB_TYPE"));
		// local file to sftp
		iBlobToFileDBO.writeTempDirFileToSFTP(
				propertyReaderHelper.getValue("sftp.local.temp.dir.mnl"),
				propertyReaderHelper.getValue("sftp.remoteHomeDirectory.mnl"));
		
		Long endTime = System.currentTimeMillis();
		LOGGER.info("Execution Time Taken:" + (endTime - startTime) / 1000
				+ "seconds");
		LOGGER.info("**************************************************");
		LOGGER.info("************Manual Loader DB DataMigrationSchd Ends************");
		LOGGER.info("**************************************************");
	}

}
