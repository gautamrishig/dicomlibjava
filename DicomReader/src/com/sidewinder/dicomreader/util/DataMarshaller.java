package com.sidewinder.dicomreader.util;

import com.sidewinder.dicomreader.dicom.vr.UnsignedValue;

public class DataMarshaller {

	private static final short BYTE_OFFSET = 8;

	public static long getDicomUnsignedLong(byte[] data)
			throws IllegalArgumentException {
		return fromByteArrayUnsigned(data, 0, UnsignedValue.UL_LENGTH);
	}

	public static long getDicomUnsignedLong(byte[] data, int arrayOffset)
			throws IllegalArgumentException {
		return fromByteArrayUnsigned(data, arrayOffset, UnsignedValue.UL_LENGTH);
	}

	public static int getDicomUnsignedShort(byte[] data) 
			throws IllegalArgumentException {
		return (int) fromByteArrayUnsigned(data, 0, UnsignedValue.US_LENGTH);
	}
	
	public static int getDicomUnsignedShort(byte[] data, int arrayOffset) 
			throws IllegalArgumentException {
		return (int) fromByteArrayUnsigned(data, arrayOffset, UnsignedValue.US_LENGTH);
	}

	private static long fromByteArrayUnsigned(byte[] data, int arrayOffset, int length)
			throws IllegalArgumentException {
		long number = 0;
		
		if (data.length - arrayOffset < length) {
			throw new IllegalArgumentException("Byte array must be at least "
					+ length + " bytes long.");
		}

		for (int i = 0; i < length; i++) {
			number |= (data[i + arrayOffset] & 0xFF) << BYTE_OFFSET * i; 
		}

		return number;
	}
}
