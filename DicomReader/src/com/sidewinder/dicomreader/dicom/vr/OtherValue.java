package com.sidewinder.dicomreader.dicom.vr;

public class OtherValue extends Value {
	
	// The DICOM standard does not prescribe any length for this vr
	public static int OB_LENGTH = -1;
	public static int OW_LENGTH = -1;
	public static int OF_LENGTH = -1;
	
	protected OtherValue(int type, byte[] data, long contentLength) {
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
		switch (type) {
		case Value.VR_OB:
			return OB_LENGTH;
		case Value.VR_OW:
			return OW_LENGTH;
		case Value.VR_OF:
			return OF_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid OtherValue type.");
		}
	}
	
	protected static boolean isCompatible(int type) {
		switch (type) {
		case Value.VR_OB:
			return true;
		case Value.VR_OW:
			return true;
		case Value.VR_OF:
			return true;
		default:
			return false;
		}
	}
}
