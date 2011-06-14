package com.sidewinder.dicomreader.dicom.vr;

public class ApplicationEntityValue extends Value<String> {
	
	public static final int AE_LENGTH = 16;
	
	protected ApplicationEntityValue(int type, long length, byte[] data, long contentLength) {
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
		return type == Value.VR_AE;
	}
}
