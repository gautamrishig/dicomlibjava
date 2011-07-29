package com.sidewinder.dicomreader.dicom.value;

import java.nio.ByteBuffer;

public class FloatingDoubleValue extends Value {
	
	protected FloatingDoubleValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, contentLength);
		return buffer.getDouble();
	}

	@Override
	protected String getStringValue() {
		return ((Double)getValue()).toString();
	}

}
