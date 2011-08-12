package com.sidewinder.dicomlib.dicom;

import java.io.File;
import java.io.FileNotFoundException;

import com.sidewinder.dicomlib.util.PositionalInputStream;

public class DicomFileContext {

	private File file;
	private DicomObject dicomObject;
	private PositionalInputStream pis;
	
	protected DicomFileContext(File file) throws FileNotFoundException {
		this.file = file;
		pis = new PositionalInputStream(file);
	}
	
	protected PositionalInputStream getPis() {
		return pis;
	}
	
	protected void setDicomObject(DicomObject dicomObject) {
		this.dicomObject = dicomObject;
	}
	
	public DicomObject getDicomObject() {
		return dicomObject;
	}
	
	public String getFilePath() {
		return file.getPath();
	}
	
	public String getFileName() {
		return file.getName();
	}
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
}
