package com.delta.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.log4j.Logger;

import com.delta.constant.GenericConstant;
import com.delta.utility.DBConnectionHelper;
import com.delta.utility.PropertyReaderHelper;

public class BlobToFileDBOImpl implements IBlobToFileDBO {
	private static final Logger LOGGER = Logger
			.getLogger(BlobToFileDBOImpl.class);
	private PropertyReaderHelper propertyReaderHelper = PropertyReaderHelper
			.getInstancePropertyReaderHelper();
	private DBConnectionHelper dbConnectionHelper = DBConnectionHelper
			.getInstanceDBConnectionHelper();
	private ISftpDBO sftpDBO = new SftpDBOImpl();

	public void lcaBlobToTempDir(String dbType) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		// String directoryPath = "D:\\BlobDownloaded\\";
		String local_temp = this.propertyReaderHelper
				.getValue("sftp.local.temp.dir.lcp");
		int processedId = 0;
		try {
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("SELECT Cvr_Rsm_Ltr_Dtls.FormId,"
					+ " Cvr_Rsm_Ltr_Dtls.CoverLetterName,"
					+ " Cvr_Rsm_Ltr_Dtls.CoverLetterFile,"
					+ " Cvr_Rsm_Ltr_Dtls.ResumeName,"
					+ " Cvr_Rsm_Ltr_Dtls.ResumeFile,"
					+ " Cvr_Rsm_Ltr_Dtls.coverLtrfolderURL,"
					+ " Cvr_Rsm_Ltr_Dtls.RecomfolderURL,"
					+ " Recomendation_dtls.CNCRR_PLT_JOB_APP_ATMT_ID AS AttachmentID,"
					+ " Recomendation_dtls.RCMND_LTTR_NM,"
					+ " Recomendation_dtls.RCMND_LTTR_OBJ"
					+ " FROM"
					+ " (SELECT CNCRR_PLT_JOB_APP_FORM_ID AS FormId,"
					+ " CVR_LTTR_NM                     AS CoverLetterName,"
					+ " CVR_LTTR_OBJ                    AS CoverLetterFile,"
					+ " RSM_NM                          AS ResumeName,"
					+ " RSM_OBJ                         AS ResumeFile,"
					+ " RCMND_IND,"
					+ "'/'"
					+ "||CNCRR_PLT_JOB_APP_FORM_ID"
					+ "||'/' AS coverLtrfolderURL ,"
					+ "'/'"
					+ "||CNCRR_PLT_JOB_APP_FORM_ID"
					+ "||'/'"
					+ "||("
					+ " CASE RCMND_IND"
					+ " WHEN 'Y'"
					+ " THEN 'Recom/'"
					+ " ELSE ''"
					+ " END) AS RecomfolderURL"
					+ " FROM CNCRR_PLT_JOB_APP"
					+ " WHERE rownum<=5"
					+ " ) Cvr_Rsm_Ltr_Dtls"
					+ " LEFT JOIN CNCRR_PLT_JOB_APP_ATMT Recomendation_dtls"
					+ " ON Cvr_Rsm_Ltr_Dtls.FormId=Recomendation_dtls.CNCRR_PLT_JOB_APP_FORM_ID"
					+ " ORDER BY Cvr_Rsm_Ltr_Dtls.FormId");
			con = this.dbConnectionHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery.toString());
			while (rs.next()) {
				if (null != rs.getString("AttachmentID")) {
					// write recommendation letter
					writeBLOBToFile(
							rs.getBinaryStream("RCMND_LTTR_OBJ"),
							local_temp + rs.getString("RecomfolderURL")
									+ rs.getString("RCMND_LTTR_NM"));
				}
				if (processedId != rs.getInt("FormId")) {
					// write cover letter
					writeBLOBToFile(
							rs.getBinaryStream("CoverLetterFile"),
							local_temp + rs.getString("coverLtrfolderURL")
									+ rs.getString("CoverLetterName"));

					// write resume letter
					writeBLOBToFile(
							rs.getBinaryStream("ResumeFile"),
							local_temp + rs.getString("coverLtrfolderURL")
									+ rs.getString("ResumeName"));

				}
				// set processed form id for avoiding duplicates
				processedId = rs.getInt("FormId");
			}
		} catch (SQLException e) {
			LOGGER.error("Error in BlobToFileDBOImpl: lcaBlobToTempDir() due to: "
					+ e.getMessage());
		} finally {
			// Cleanup resources
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void MsgBoardBolbToTempDir(String dbType) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		// String directoryPath = "D:\\BlobDownloaded\\";
		String local_temp = this.propertyReaderHelper
				.getValue("sftp.local.temp.dir.msgbrd");
		try {
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("select CNCRR_MSG_ID as formId,"
					+ "CNCRR_MSG_ATMT_IMG_OBJ as attachFile,"
					+ "CNCRR_MSG_ATMT_FILE_NM as fileName,"
					+ "'/'||CNCRR_MSG_ID||'/' as msgBrdDirUrl, "
					+ " CNCRR_MSG_ATMT_ID as attachId"
					+ " from cncrr_msg_brd_atmt " 
					+ " where rownum<=10"
					+ " order by CNCRR_MSG_ID ");
			con = this.dbConnectionHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery.toString());
			while (rs.next()) {
				// write Attachment file
				writeBLOBToFile(
						rs.getBinaryStream("attachFile"),
						local_temp + rs.getString("msgBrdDirUrl")
								+ rs.getString("attachId")
								+ GenericConstant.UNDERSCORE
								+ rs.getString("fileName"));
			}
		} catch (SQLException e) {
			LOGGER.error("Error in BlobToFileDBOImpl: MsgBoardBolbToTempDir() due to: "
					+ e.getMessage());
		} finally {
			// Cleanup resources
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
			e.printStackTrace();
			}
		}

	}

	public void MNLBolbToTempDir(String dbType) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		// String directoryPath = "D:\\BlobDownloaded\\";
		String local_temp = this.propertyReaderHelper
				.getValue("sftp.local.temp.dir.mnl");
		try {
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append(" SELECT grpSgrpDlts.CNCRR_DEPT_CD   AS DeptCode, "
					+ " grpSgrpDlts.CNCRR_MNL_GRP_CD     AS GroupCode, "
					+ " grpSgrpDlts.CNCRR_MNL_SGRP_CD    AS SubGroupCode, "
					+ " mnlContentDtls.CNCRR_MNL_URL_IND AS URLStatus, "
					+ " mnlContentDtls.CNCRR_MNL_FILE_NM AS fileName, "
					+ " mnlContentDtls.CNCRR_MNL_OBJ     AS fileobj, "
					+ " '/' "
					+ " ||grpSgrpDlts.CNCRR_DEPT_CD "
					+ " ||'/' "
					+ " ||grpSgrpDlts.CNCRR_MNL_GRP_CD "
					+ " ||'/' "
					+ " ||grpSgrpDlts.CNCRR_MNL_SGRP_CD "
					+ " ||'/' AS ManualFolderURL "
					+ " FROM EEW.CNCRR_MNL_GRP_SGRP_DEPT grpSgrpDlts "
					+ " LEFT JOIN EEW.CNCRR_MNL_CTNT mnlContentDtls "
					+ " ON (grpSgrpDlts.CNCRR_DEPT_CD = mnlContentDtls.CNCRR_DEPT_CD "
					+ " AND grpSgrpDlts.CNCRR_MNL_GRP_CD = mnlContentDtls.CNCRR_MNL_GRP_CD "
					+ " AND grpSgrpDlts.CNCRR_MNL_SGRP_CD = mnlContentDtls.CNCRR_MNL_SGRP_CD) "
					+ " WHERE rownum<= 10 " + " ORDER BY DeptCode,"
					+ " GroupCode, " + " SubGroupCode, " + " URLStatus ");
			con = this.dbConnectionHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery.toString());
			while (rs.next()) {
				if (GenericConstant.NO_INDICATOR.equals(rs
						.getString("URLStatus"))) {
					// write manual loader file
					writeBLOBToFile(
							rs.getBinaryStream("fileobj"),
							local_temp + rs.getString("ManualFolderURL")
									+ rs.getString("fileName"));	
				}else{
					//create folder
					createDir(local_temp + rs.getString("ManualFolderURL"));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Error in BlobToFileDBOImpl: MNLBolbToTempDir() due to: "
					+ e.getMessage());
		} finally {
			// Cleanup resources
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void MemoToTempDir(String dbType) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String local_temp = this.propertyReaderHelper
				.getValue("sftp.local.temp.dir.memo");
		try {
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append(" SELECT CNCRR_MEMO_ID  AS MemoId, "
					+ " CNCRR_MEMO_FILE_OBJ AS attachFile, "
					+ " CNCRR_MEMO_FILE_NM  AS fileName, " 
					+ " '/' "
					+ " ||CNCRR_MEMO_ID "
					+ " ||'/' AS memoDirUrl, "
					+ " CNCRR_MEMO_ATMT_ID AS attachId "
					+ " FROM CNCRR_MEMO_ATMT " 
					+ " WHERE rownum<=10 "
					+ " ORDER BY CNCRR_MEMO_ID");
			con = this.dbConnectionHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery.toString());
			while (rs.next()) {
				// write Attachment file
				writeBLOBToFile(
						rs.getBinaryStream("attachFile"),
						local_temp + rs.getString("memoDirUrl")
								+ rs.getString("attachId")
								+ GenericConstant.UNDERSCORE
								+ rs.getString("fileName"));
			}
		} catch (SQLException e) {
			LOGGER.error("Error in BlobToFileDBOImpl: MemoToTempDir() due to: "
					+ e.getMessage());
		} finally {
			// Cleanup resources
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean createDir(String destFileImage) {
		boolean flag = false;
		File fileImage = new File(destFileImage);
		// if directory doesn't exists create
		if (!fileImage.exists()) {
			// to create directories
			flag=fileImage.mkdirs();
			LOGGER.info("Directory created with name : " + fileImage.getName());
		}
		return flag;
	}
	public boolean writeBLOBToFile(InputStream ins, String destFileImage) {
		boolean flag = false;
		FileOutputStream fos = null;
		try {
			if (null != ins) {
				// copy recommendation files first
				File fileImage = new File(destFileImage);
				// if directory doesn't exists create one
				if (!fileImage.getParentFile().exists()) {
					// to create parent directories
					fileImage.getParentFile().mkdirs();
					LOGGER.info("Directory created with name : "
							+ fileImage.getParentFile().getName());
				}
				// check if file already exists then don't write
				if (!fileImage.exists()) {
					fos = new FileOutputStream(fileImage);
					// write to file
					byte[] buffer = new byte[1024];
					while (ins.read(buffer) > 0) {
						fos.write(buffer);
					}
					flag = true;
				} else {
					LOGGER.info("File already exists : " + fileImage.getName());
					flag = false;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occured while writing to file: due to : "
					+ e.getMessage());
		} finally {
			// Cleanup resources
			try {
				if (null != fos)
					fos.close();
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				LOGGER.error("Exception occured while closing resource : due to : "
						+ ioe.getMessage());
			}
		}
		return flag;
	}

	public void writeTempDirFileToSFTP(String local_dir_path,
			String sftp_home_dir_Path) {
		// get all the files from a directory and copy them to sftp server
		listDirectory(local_dir_path, 0, sftp_home_dir_Path);
	}

	public void listDirectory(String local_dir_path, int level,
			String sftp_home_dir_Path) {
		File local_dir = new File(local_dir_path);
		File[] local_firstLevel_Files = local_dir.listFiles();
		if (local_firstLevel_Files != null && local_firstLevel_Files.length > 0) {
			for (File local_aFile : local_firstLevel_Files) {
				for (int i = 0; i < level; i++) {
					System.out.print("\t");
				}
				try {
					if (local_aFile.isDirectory()) {
						// if a directory
						sftpDBO.doCreateDirectory(sftp_home_dir_Path
								+ File.separator + local_aFile.getName());
						listDirectory(local_aFile.getAbsolutePath(), level + 1,
								sftp_home_dir_Path + File.separator
										+ local_aFile.getName());
					} else {
						//upload file to SFTP server
						sftpDBO.upload(local_aFile, sftp_home_dir_Path);
					}
				} catch (FileSystemException e) {
					LOGGER.error("FileSystemException in BlobToFileDBOImpl: listDirectory() due to : "
							+ e.getMessage());
				}
			}
		}
	}
}
