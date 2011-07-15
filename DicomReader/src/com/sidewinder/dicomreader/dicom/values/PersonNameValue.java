package com.sidewinder.dicomreader.dicom.values;

//TODO: Complete the implementation of this Value
// The single components still have to be separated
// and stored in an appropriate value
public class PersonNameValue extends Value {
	
	protected PersonNameValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return new String(data, 0, (int) contentLength);
	}

	@Override
	protected String getStringValue() {
		return ((String) getValue()).replace("^", " ");
	}

}
