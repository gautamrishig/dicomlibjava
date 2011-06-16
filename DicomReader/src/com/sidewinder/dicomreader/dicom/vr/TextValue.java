package com.sidewinder.dicomreader.dicom.vr;

// TODO: this class only contains a 128 bytes max preview of the actual content
// Change the implementation to provide adequate means to retrieve the rest
// of the content
public class TextValue extends Value {
	
	public static final int ST_LENGTH = 1024;
	public static final int LT_LENGTH = 10240;
	public static final long UT_LENGTH = 4294967294L;
	
	protected TextValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return new String(data, 0, (int) contentLength);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		switch (type) {
		case Value.VR_ST:
			return ST_LENGTH;
		case Value.VR_LT:
			return LT_LENGTH;
		case Value.VR_UT:
			return UT_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid TextValue type.");
		}
	}

	protected static boolean isCompatible(int type) {
		switch (type) {
		case Value.VR_ST:
		case Value.VR_LT:
		case Value.VR_UT:
			return true;
		default:
			return false;
		}
	}
}
