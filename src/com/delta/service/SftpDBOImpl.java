package com.delta.service;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;

import com.delta.utility.SftpHelper;

public class SftpDBOImpl implements ISftpDBO {
	private static final Logger LOGGER = Logger.getLogger(SftpDBOImpl.class);

	/**
	 * Method to setup default SFTP config
	 * 
	 * @return the FileSystemOptions object containing the specified
	 *         configuration options
	 * @throws FileSystemException
	 */
	public static FileSystemOptions createDefaultOptions()
			throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();
		// SSH Key checking
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				opts, "no");
		/*
		 * Using the following line will cause VFS to choose File System's Root
		 * as VFS's root. If I wanted to use User's home as VFS's root then set
		 * 2nd method parameter to "true"
		 */
		// Root directory set to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
		Integer milisecondsInt = new Integer(10000);
		// Timeout is count by Milliseconds
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts,
				milisecondsInt);
		return opts;
	}

	public boolean upload(File localFile, String remote_home_dir_path)
			throws FileSystemException {
		boolean flag = false;
		SftpHelper sftpHelper = SftpHelper.getInstanceSftpHelper();
		StandardFileSystemManager manager = sftpHelper.getManager();
		try {
			manager.init();
			// Create local file object
			FileObject localFileObj = manager.resolveFile(localFile
					.getAbsolutePath());
			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(sftpHelper.getSFTP_SERVER_ADDRESS(),
							sftpHelper.getSFTP_USERNAME(),
							sftpHelper.getSFTP_PASSWORD(), remote_home_dir_path
									+ File.separator + localFile.getName()),
					createDefaultOptions());
			if (!remoteFile.exists()) {
				// upload local file to sftp server
				remoteFile.copyFrom(localFileObj, Selectors.SELECT_SELF);
				flag = true;
				LOGGER.info("File Uploaded to SFTP server with name: "
						+ remoteFile.getName().getBaseName());
			} else {
				flag = false;
				LOGGER.info("File with name: "
						+ remoteFile.getName().getBaseName()+" already exists");
			}
		} catch (FileSystemException fse) {
			LOGGER.error("File Uploading failed due to : " + fse.getMessage());
			throw fse;
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
		return flag;
	}

	public String createConnectionString(String hostName, String username,
			String password, String remoteFilePath) {
		return "sftp://" + username + ":" + password + "@" + hostName + "/"
				+ remoteFilePath;
	}

	public boolean exist(File localFile, String remoteFilePath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean download(File localFile, String remoteFilePath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean move(File localFile, String remoteTempFilePath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean delete(File localFile, String remoteFilePath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean doCreateDirectory(String remoteDirPath)
			throws FileSystemException {
		boolean flag = false;
		SftpHelper sftpHelper = SftpHelper.getInstanceSftpHelper();
		StandardFileSystemManager manager = sftpHelper.getManager();
		try {
			manager.init();
			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(sftpHelper.getSFTP_SERVER_ADDRESS(),
							sftpHelper.getSFTP_USERNAME(),
							sftpHelper.getSFTP_PASSWORD(), remoteDirPath),
					createDefaultOptions());
			if (!remoteFile.exists()) {
				remoteFile.createFolder();
				flag = true;
				LOGGER.info("Directory creation successful in SFTP server with name : ["
						+ remoteFile.getName().getBaseName() + " ]");
			} else {
				flag = false;
				LOGGER.info("Directory with name : [" + remoteFile.getName().getBaseName()
						+ " ] already exists");
			}
		} catch (FileSystemException fse) {
			System.out.println("File Uploading failed due to : "
					+ fse.getMessage());
			LOGGER.error("File Uploading failed due to : " + fse.getMessage());
			throw fse;
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
		// TODO Auto-generated method stub
		return flag;
	}

}
