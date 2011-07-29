package com.sidewinder.dicomlib.dicom.value;

/**
 * This class is used to represent the CS, SH and LO Value Representations
 * 
 * @author sidewinder
 *
 */
public abstract class StringValue extends Value {
	
	protected StringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected String fromByteArray(byte[] data, int contentLength) 
			throws IllegalArgumentException {
		// Reject value creation if the length of the buffer exceeds
		// the maximum Integer number
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Content is too long for " +
					"a StringValue.");
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
