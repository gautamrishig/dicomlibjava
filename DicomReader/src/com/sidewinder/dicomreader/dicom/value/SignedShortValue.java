package com.sidewinder.dicomreader.dicom.value;

import java.nio.ByteBuffer;

public class SignedShortValue extends Value {

	protected SignedShortValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, contentLength);
		return buffer.getShort();
	}

	@Override
	protected String getStringValue() {
		return ((Short)getValue()).toString();
	}

}
