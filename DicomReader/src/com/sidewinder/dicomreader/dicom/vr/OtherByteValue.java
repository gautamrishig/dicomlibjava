package com.sidewinder.dicomreader.dicom.vr;

public class OtherByteValue extends Value {
	
	// The DICOM standard does not prescribe any length for this vr
	public static int OB_LENGTH = -1;
	
	protected OtherByteValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return data;
	}

	@Override
	protected String getStringValue() {
		return "Byte array, " + getContentLength() + " bytes long";
	}

	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		if (type == Value.VR_OB) {
			return OB_LENGTH;
		} else {
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid OtherByteValue type.");
		}
	}
	
	protected static boolean isCompatible(int type) {
		return type == Value.VR_OB;
	}
}
