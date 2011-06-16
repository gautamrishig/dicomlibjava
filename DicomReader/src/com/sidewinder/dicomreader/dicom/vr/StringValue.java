package com.sidewinder.dicomreader.dicom.vr;

/**
 * This class is used to represent the CS, SH and LO Value Representations
 * 
 * @author sidewinder
 *
 */
public class StringValue extends Value {
	
	public static final int CS_LENGTH = 16;
	public static final int SH_LENGTH = 16;
	public static final int LO_LENGTH = 64;
	
	protected StringValue(int type, byte[] data, long contentLength) {
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
	
	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		switch (type) {
		case Value.VR_CS:
			return CS_LENGTH;
		case Value.VR_SH:
			return SH_LENGTH;
		case Value.VR_LO:
			return LO_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid StringValue type.");
		}
	}
	
	protected static boolean isCompatible(int type) {
		switch (type) {
		case Value.VR_CS:
		case Value.VR_SH:
		case Value.VR_LO:
			return true;
		default:
			return false;
		}
	}
}
