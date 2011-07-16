package com.sidewinder.dicomreader.dicom.values;

import com.sidewinder.dicomreader.util.DataMarshaller;

public class UnsignedShortValue extends UnsignedValue {

	protected UnsignedShortValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}
	
	@Override
	protected Integer fromByteArray(byte[] data, int dataLength) {
		return DataMarshaller.getDicomUnsignedShort(data);
	}
	
}
