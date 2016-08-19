package com.delta.service;

import java.io.InputStream;

public interface IBlobToFileDBO {
	//convert LCA blob columns to folder and write files 
	public void lcaBlobToTempDir(String dbType);
	//convert MessageBoard blob columns to folder and write files 
	public void MsgBoardBolbToTempDir(String dbType);
	public boolean writeBLOBToFile(InputStream ins, String destFileImage);
	public void writeTempDirFileToSFTP(String localpath,String destFileImage);
}
