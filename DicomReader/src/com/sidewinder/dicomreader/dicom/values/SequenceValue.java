package com.sidewinder.dicomreader.dicom.values;

import java.util.List;

import com.sidewinder.dicomreader.dicom.DicomObject;

public class SequenceValue extends Value {
	
	protected SequenceValue(List<DicomObject> value, long contentLength) {
		super(Value.VR_SQ, value, contentLength);
	}
	
	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return null;
	}
	
	@Override
	protected String getStringValue() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		
		for (DicomObject dicomObject : (List<DicomObject>)getValue()) {
			builder.append("Dicom Object " + i++ + "\n");
			builder.append(dicomObject.toString());
		}
		
		return builder.toString();
	}
	
}
