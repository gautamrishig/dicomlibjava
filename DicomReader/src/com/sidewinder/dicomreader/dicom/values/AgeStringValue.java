package com.sidewinder.dicomreader.dicom.values;

public class AgeStringValue extends Value {
	
	protected AgeStringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		int dicomLength = getDicomLength();
		
		if (contentLength != dicomLength || data.length < dicomLength) {
			throw new IllegalArgumentException("AS Value Representation " +
					"must be exactly " + dicomLength + " bytes long.");
		}
		
		return new String(data, 0, (int) contentLength);
	}

	@Override
	protected String getStringValue() {
		return (String) getValue();
	}
	
}
