package com.delta.service;

import java.io.File;

import org.apache.commons.vfs2.FileSystemException;

public interface ISftpDBO {
	/**
	 * Method to upload a file in Remote server
	 * 
	 * @param remoteFilePath
	 *            remoteFilePath. Should contain the entire remote file path -
	 *            Directory and Filename with / as separator
	 */
	public boolean upload(File localFile, String remoteFilePath)
			throws FileSystemException;

	public boolean exist(File localFile, String remoteFilePath);

	public boolean download(File localFile, String remoteFilePath);

	public boolean move(File localFile, String remoteTempFilePath);

	public boolean delete(File localFile, String remoteFilePath);

	public String createConnectionString(String hostName, String username,
			String password, String remoteFilePath);

	public boolean doCreateDirectory(String remoteFilePath)
			throws FileSystemException;
}
