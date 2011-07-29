package com.sidewinder.dicomlib.dicom.value;

public class LongStringValue extends StringValue {

	protected LongStringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
