package com.sidewinder.dicomlib.util;

public class DataMarshaller {

	private static final int UNSIGNED_LONG_LENGTH = 4;
	private static final int UNSIGNED_SHORT_LENGTH = 2;
	
	private static final short BYTE_OFFSET = 8;

	public static long getDicomUnsignedLong(byte[] data)
			throws IllegalArgumentException {
		return fromByteArrayUnsigned(data, 0, UNSIGNED_LONG_LENGTH);
	}

	public static long getDicomUnsignedLong(byte[] data, int arrayOffset)
			throws IllegalArgumentException {
		return fromByteArrayUnsigned(data, arrayOffset, UNSIGNED_LONG_LENGTH);
	}

	public static int getDicomUnsignedShort(byte[] data) 
			throws IllegalArgumentException {
		return (int) fromByteArrayUnsigned(data, 0, UNSIGNED_SHORT_LENGTH);
	}
	
	public static int getDicomUnsignedShort(byte[] data, int arrayOffset) 
			throws IllegalArgumentException {
		return (int) fromByteArrayUnsigned(data, arrayOffset, UNSIGNED_SHORT_LENGTH);
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
