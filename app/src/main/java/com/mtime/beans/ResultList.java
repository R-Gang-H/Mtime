// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class ResultList extends MBaseBean {
	private String UploadId;
	private String FileName;
	private String FileFormat;
	private int FileContentLength;
	private String Src;
	private int ErrorCode;

	public String getUploadId() {
		return UploadId;
	}

	public void setUploadId(String UploadId) {
		this.UploadId = UploadId;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String FileName) {
		this.FileName = FileName;
	}

	public String getFileFormat() {
		return FileFormat;
	}

	public void setFileFormat(String FileFormat) {
		this.FileFormat = FileFormat;
	}

	public int getFileContentLength() {
		return FileContentLength;
	}

	public void setFileContentLength(int FileContentLength) {
		this.FileContentLength = FileContentLength;
	}

	public String getSrc() {
		return Src;
	}

	public void setSrc(String Src) {
		this.Src = Src;
	}

	public int getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(int ErrorCode) {
		this.ErrorCode = ErrorCode;
	}
}
