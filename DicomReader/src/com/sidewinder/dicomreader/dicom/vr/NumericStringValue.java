package com.sidewinder.dicomreader.dicom.vr;

public class NumericStringValue extends Value {
	
	public static final int IS_LENGTH = 12;
	public static final int DS_LENGTH = 16;
	
	protected NumericStringValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		// Reject value creation if the length of the buffer exceeds
		// the maximum Integer number
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Content is too long for " +
					"a NumericStringValue.");
		}
		
		// Trimming String to avoid DICOM trailing spaces
		String value = new String(data, 0, (int)contentLength).trim();
		return value;
	}

	@Override
	protected String getStringValue() {
		return (String) getValue();
	}

	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		switch (type) {
		case Value.VR_IS:
			return IS_LENGTH;
		case Value.VR_DS:
			return DS_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid NumericStringValue type.");
		}
	}

	protected static boolean isCompatible(int type) {
		switch (type) {
		case Value.VR_IS:
			return true;
		case Value.VR_DS:
			return true;
		default:
			return false;
		}
	}
}
