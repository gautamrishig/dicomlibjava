package com.sidewinder.dicomreader.dicom.values;

import com.sidewinder.dicomreader.util.DataMarshaller;

public abstract class UnsignedValue extends Value {
	
	protected UnsignedValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Long fromByteArray(byte[] data, long dataLength) {
		return DataMarshaller.getDicomUnsignedLong(data);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

}
