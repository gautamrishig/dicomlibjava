package com.sidewinder.dicomreader.dicom.values;

// TODO: this class only contains a 128 bytes max preview of the actual content
// Change the implementation to provide adequate means to retrieve the rest
// of the content
public abstract class TextValue extends Value {
	
	protected TextValue(int type, byte[] data, int contentLength) {
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
