package com.sidewinder.dicomreader.dicom;

import java.util.List;

import com.sidewinder.dicomreader.dicom.dicomelement.DicomElement;

public class DicomObject {

	private List<DicomElement> dicomElements;
	
	protected DicomObject(List<DicomElement> dicomElementList) {
		this.dicomElements = dicomElements;
	}
}
