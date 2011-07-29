package com.sidewinder.dicomlib.dicom;

import java.util.List;


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
