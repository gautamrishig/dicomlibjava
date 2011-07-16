package com.sidewinder.dicomreader.dicom.values;

import com.sidewinder.dicomreader.util.DataMarshaller;

public class UnsignedLongValue extends UnsignedValue {

	protected UnsignedLongValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected Long fromByteArray(byte[] data, int dataLength) {
		return DataMarshaller.getDicomUnsignedLong(data);
	}
	
}
