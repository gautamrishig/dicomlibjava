package com.sidewinder.dicomreader.dicom.values;

public class UnknownValue extends OtherByteValue {

	protected UnknownValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
