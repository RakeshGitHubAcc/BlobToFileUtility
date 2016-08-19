package com.delta.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyReaderHelper {
	private static final Logger LOGGER = Logger
			.getLogger(PropertyReaderHelper.class);
	private static PropertyReaderHelper instance;
	private Properties properties;
	// the base folder is ./, the root of the ApplicationResource.properties
	// file
	private final String path = "./ApplicationResource.properties";

	private PropertyReaderHelper() {
		FileInputStream file = null;
		try {
			this.properties = new Properties();
			// load the file handle for main.properties
			file = new FileInputStream(this.path);
			// load all the properties from this file
			properties.load(file);
			// we have loaded the properties, so close the file handle
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
	//thread safe double locking mode of lazy initialization
	public static PropertyReaderHelper getInstancePropertyReaderHelper() {
		if (instance == null) {
			synchronized (PropertyReaderHelper.class) {
				if (instance == null) {
					instance = new PropertyReaderHelper();
				}
			}
		}
		return instance;
	}

	public String getValue(String propKey) {
		return this.properties.getProperty(propKey);
	}
}
