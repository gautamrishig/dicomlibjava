package com.sidewinder.dicomreader.dicom.vr;

public class PersonNameValue extends Value {
	
	protected PersonNameValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long getDicomLength(int type) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
