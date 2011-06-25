package com.sidewinder.dicomreader.dicom.vr;

public class AgeStringValue extends Value {
	
	
	public static final int AS_LENGTH = 4;
	
	protected AgeStringValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		if (contentLength != AS_LENGTH || data.length < 4) {
			throw new IllegalArgumentException("AS Value Representation " +
					"must be 4 bytes long.");
		}
		
		return new String(data, 0, (int) contentLength);
	}

	@Override
	protected String getStringValue() {
		return (String) getValue();
	}

	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		if (isCompatible(type)) {
			return AS_LENGTH;
		}
		
		throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid AgeStringValue type.");
	}

	protected static boolean isCompatible(int type) {
		return type == Value.VR_AS;
	}
}
