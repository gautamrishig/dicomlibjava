package com.sidewinder.dicomreader.dicom.vr;

public class UniqueIdentifierValue extends Value {
	
	public static final int UI_LENGTH = 64;
	
	protected UniqueIdentifierValue (int type, byte[] data,
			long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected String fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		// Reject value creation if the length of the buffer exceeds
		// the maximum Integer number
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		// Trimming String to avoid DICOM trailing spaces
		String value = new String(data, 0, (int)contentLength).trim();
		return value;
	}

	@Override
	protected String getStringValue() {
		return (String) getValue();
	}
	
	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		if (type != Value.VR_UI) {
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid UniqueIdentifierValue type.");
		}
		
		return UI_LENGTH;
	}

	protected static boolean isCompatible(int type) {
		return type == Value.VR_UI; 
	}
}
