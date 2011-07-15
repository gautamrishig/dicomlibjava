package com.sidewinder.dicomreader.dicom.values;

public class UnlimitedText extends TextValue {

	protected UnlimitedText(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}
	
}
