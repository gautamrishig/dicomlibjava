package com.sidewinder.dicomreader.dicom.vr;

/**
 * This class is used to represent the CS, SH and LO Value Representations
 * 
 * @author sidewinder
 *
 */
public class StringValue extends Value<String> {
	
	public static final int CS_LENGTH = 16;
	public static final int SH_LENGTH = 16;
	public static final int LO_LENGTH = 64;
	
	protected StringValue(int type, long length, byte[] data, long contentLength) {
		super(type, length, data, contentLength);
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
		return getValue();
	}
	
	protected static boolean isCompatible(int type) {
		if (type == Value.VR_CS || type == Value.VR_SH ||
				type == Value.VR_LO) {
			return true;
		} else {
			return false;
		}
	}
}
