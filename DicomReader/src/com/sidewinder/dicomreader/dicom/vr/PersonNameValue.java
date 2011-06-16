package com.sidewinder.dicomreader.dicom.vr;

public class PersonNameValue extends Value {
	
	// 64 char max for every single element
	public static final int ELEMENTS = 5;
	public static final int SINGLE_ELEMENT_MAX_LENGTH = 64;
	public static final int PN_LENGTH = SINGLE_ELEMENT_MAX_LENGTH * ELEMENTS;
	
	protected PersonNameValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return new String(data, 0, (int) contentLength);
	}

	@Override
	protected String getStringValue() {
		return ((String) getValue()).replace("^", " ");
	}

	@Override
	protected long getDicomLength(int type) {
		return PN_LENGTH;
	}
	
	protected static boolean isCompatible(int type) {
		return type == Value.VR_PN;
	}
}
