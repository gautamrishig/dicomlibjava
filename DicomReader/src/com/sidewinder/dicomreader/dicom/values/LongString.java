package com.sidewinder.dicomreader.dicom.values;

public class LongString extends StringValue {

	protected LongString(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}
	
}
