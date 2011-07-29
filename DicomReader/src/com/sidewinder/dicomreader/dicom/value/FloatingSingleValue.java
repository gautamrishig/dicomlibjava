package com.sidewinder.dicomreader.dicom.value;

import java.nio.ByteBuffer;

public class FloatingSingleValue extends Value {

	protected FloatingSingleValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, contentLength);
		return buffer.getFloat();
	}

	@Override
	protected String getStringValue() {
		return ((Float)getValue()).toString();
	}

}
