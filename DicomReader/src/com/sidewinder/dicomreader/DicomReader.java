package com.sidewinder.dicomreader;

import com.sidewinder.dicomreader.dicom.DicomFile;
import com.sidewinder.dicomreader.exception.MalformedDicomException;

public class DicomReader {

	public static void main(String[] args) throws MalformedDicomException {
		new DicomFile("/Users/sidewinder/Desktop/dicom/IM-0001-0008.dcm");
		//new DicomFile("/Users/sidewinder/Desktop/dicom/US-RGB-8-esopecho.dcm");
	}
	
}
