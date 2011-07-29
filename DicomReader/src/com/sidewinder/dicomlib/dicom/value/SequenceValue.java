package com.sidewinder.dicomlib.dicom.value;

import java.util.List;

import com.sidewinder.dicomlib.dicom.DicomObject;

public class SequenceValue extends Value {
	
	protected SequenceValue(List<DicomObject> value, int contentLength) {
		super(Value.SQ, value, contentLength);
	}
	
	@Override
	protected List<DicomObject> fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		return null;
	}
	
	@Override
	protected String getStringValue() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n");
		
		for (DicomObject dicomObject : (List<DicomObject>)getValue()) {
			builder.append(">");
			builder.append(dicomObject.toString().replace("\n", "\n>"));
		}
		
		return builder.toString();
	}
	
}
