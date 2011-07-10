package com.sidewinder.dicomreader.dicom;

import java.util.List;

import com.sidewinder.dicomreader.dicom.dicomelement.DicomElement;

public class DicomObject {

	private List<DicomElement> dicomElementList;
	
	protected DicomObject(List<DicomElement> dicomElementList) {
		this.dicomElementList = dicomElementList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (DicomElement dicomElement : dicomElementList) {
			builder.append(dicomElement.toString());
			builder.append("\n");
		}
		
		return builder.toString();
	}
}
