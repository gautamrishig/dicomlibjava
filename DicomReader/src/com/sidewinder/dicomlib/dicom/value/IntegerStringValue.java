package com.sidewinder.dicomlib.dicom.value;

public class IntegerStringValue extends NumericStringValue {

	protected IntegerStringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
