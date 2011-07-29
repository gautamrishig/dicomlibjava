package com.sidewinder.dicomlib.dicom.value;

public abstract class UnsignedValue extends Value {
	
	protected UnsignedValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

}
