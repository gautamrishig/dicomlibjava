package com.sidewinder.dicomreader.dicom.values;

public abstract class UnsignedValue extends Value {
	
	protected UnsignedValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

}
