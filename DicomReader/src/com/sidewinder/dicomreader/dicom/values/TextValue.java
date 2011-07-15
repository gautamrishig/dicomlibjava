package com.sidewinder.dicomreader.dicom.values;

// TODO: this class only contains a 128 bytes max preview of the actual content
// Change the implementation to provide adequate means to retrieve the rest
// of the content
public abstract class TextValue extends Value {
	
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

}
