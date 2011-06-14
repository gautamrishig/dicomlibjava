package com.sidewinder.dicomreader.dicom.vr;

import com.sidewinder.dicomreader.util.DataMarshaller;

public class UnsignedValue extends Value<Long> {
	
	public static final int US_LENGTH = 2;
	public static final int UL_LENGTH = 4;
	
	protected UnsignedValue(int type, long length, byte[] data, long dataLength) {
		super(type, length, data, dataLength);
	}

	@Override
	protected Long fromByteArray(byte[] data, long dataLength) {
		return DataMarshaller.getDicomUnsignedLong(data);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

	protected static boolean isCompatible(int type) {
		if (type == Value.VR_US || type == Value.VR_UL) {
			return true;
		} else {
			return false;
		}
	}
}
