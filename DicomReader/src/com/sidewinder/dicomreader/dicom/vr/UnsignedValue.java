package com.sidewinder.dicomreader.dicom.vr;

import com.sidewinder.dicomreader.util.DataMarshaller;

public class UnsignedValue extends Value {
	
	public static final int US_LENGTH = 2;
	public static final int UL_LENGTH = 4;
	
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
	
	@Override
	protected long getDicomLength(int type) {
		switch (type) {
		case Value.VR_US:
			return US_LENGTH;
		case Value.VR_UL:
			return UL_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid UnsignedValue type.");
		}
	}

	protected static boolean isCompatible(int type) {
		if (type == Value.VR_US || type == Value.VR_UL) {
			return true;
		} else {
			return false;
		}
	}
}
