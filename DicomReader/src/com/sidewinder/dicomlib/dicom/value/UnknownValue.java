package com.sidewinder.dicomlib.dicom.value;

public class UnknownValue extends OtherByteValue {

	protected UnknownValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
