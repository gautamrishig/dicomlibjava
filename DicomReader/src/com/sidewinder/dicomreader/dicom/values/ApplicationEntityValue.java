package com.sidewinder.dicomreader.dicom.values;

public class ApplicationEntityValue extends Value {
		
	protected ApplicationEntityValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected String fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		// Reject value creation if the length of the buffer exceeds
		// the maximum Integer number
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		// Trimming String to avoid DICOM trailing spaces
		String value = new String(data, 0, (int)contentLength).trim();
		return value;
	}

	@Override
	protected String getStringValue() {
		return (String) getValue();
	}

}
