package com.sidewinder.dicomlib;

import com.sidewinder.dicomlib.dicom.DicomFileReader;
import com.sidewinder.dicomlib.exception.MalformedDicomException;

public class DicomReader {

	public static void main(String[] args) throws MalformedDicomException {
		//new DicomFile("/Users/sidewinder/Desktop/dicom/IM-0001-0008.dcm");
		//new DicomFile("/Users/sidewinder/Desktop/dicom/US-RGB-8-esopecho.dcm");
		//new DicomFile("/Users/sidewinder/Desktop/dicom/B7QJ54U4.dcm");
		//new DicomFile("/Users/sidewinder/Desktop/dicom/testGE/B7SG6E8C.dcm");
		new DicomFileReader("/Users/sidewinder/Desktop/dicom/testGE/B7SG6E8E.dcm");
	}
	
}
