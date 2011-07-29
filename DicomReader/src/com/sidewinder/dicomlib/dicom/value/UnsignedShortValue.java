package com.sidewinder.dicomlib.dicom.value;

import com.sidewinder.dicomlib.util.DataMarshaller;

public class UnsignedShortValue extends UnsignedValue {

	protected UnsignedShortValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected Integer fromByteArray(byte[] data, int dataLength) {
		return DataMarshaller.getDicomUnsignedShort(data);
	}
	
}
