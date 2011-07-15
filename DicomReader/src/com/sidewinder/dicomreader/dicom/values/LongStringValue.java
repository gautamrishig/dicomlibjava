package com.sidewinder.dicomreader.dicom.values;

public class LongStringValue extends StringValue {

	protected LongStringValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
