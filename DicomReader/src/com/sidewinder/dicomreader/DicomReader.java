package com.sidewinder.dicomreader;

import com.sidewinder.dicomreader.dicom.DicomFile;

public class DicomReader {

	public static void main(String[] args) {
		new DicomFile("/Users/sidewinder/Desktop/dicom/IM-0001-0008.dcm");
		//new DicomFile("/Users/sidewinder/Desktop/dicom/US-RGB-8-esopecho.dcm");
	}
	
}
