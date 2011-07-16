package com.sidewinder.dicomreader.dicom.value;

public class UnlimitedTextValue extends TextValue {

	protected UnlimitedTextValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
}
