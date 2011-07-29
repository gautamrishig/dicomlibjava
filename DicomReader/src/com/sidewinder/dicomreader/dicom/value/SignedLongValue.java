package com.sidewinder.dicomreader.dicom.value;

import java.nio.ByteBuffer;

public class SignedLongValue extends Value {
	
	protected SignedLongValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, contentLength);
		return buffer.getInt();
	}

	@Override
	protected String getStringValue() {
		return ((Integer)getValue()).toString();
	}

}
