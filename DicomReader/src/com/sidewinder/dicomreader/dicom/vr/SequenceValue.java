package com.sidewinder.dicomreader.dicom.vr;

import java.util.List;

import com.sidewinder.dicomreader.dicom.DicomObject;

public class SequenceValue extends Value {

	public static final int SQ_LENGTH = -1;
	
	protected SequenceValue(List<DicomObject> value, long contentLength) {
		super(Value.VR_SQ, value, contentLength);
	}
	
	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected long getDicomLength(int type) {
		return SQ_LENGTH;
	}
	
	@Override
	protected String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected static boolean isCompatible(int type) {
		return type == Value.VR_SQ;
	}
}
