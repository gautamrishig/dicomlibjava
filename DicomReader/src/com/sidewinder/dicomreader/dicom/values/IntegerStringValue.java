package com.sidewinder.dicomreader.dicom.values;

public class IntegerStringValue extends NumericStringValue {

	protected IntegerStringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
