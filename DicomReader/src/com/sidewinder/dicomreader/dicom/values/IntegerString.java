package com.sidewinder.dicomreader.dicom.values;

public class IntegerString extends NumericStringValue {

	protected IntegerString(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}
	
}
