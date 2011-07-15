package com.sidewinder.dicomreader.dicom.values;

// TODO: implement byte swapping for little/big endian
// The data could be stored as read, and store the latest
// modified endiannes version, along with a variable that
// is used to store the current endianness (so it is possible
// to check if the current stored version is consistent with
// the DICOM transfer syntax
public abstract class OtherValue extends Value {
	
	protected OtherValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		return data;
	}

	@Override
	protected String getStringValue() {
		return "Byte array, " + getContentLength() + " bytes long";
	}

}