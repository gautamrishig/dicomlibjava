package com.sidewinder.dicomreader.dicom.dicomelement;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public class NormalDicomElement extends DicomElement {
	
	protected NormalDicomElement(Tag tag, Value value) {
		super(tag, value);
	}
	
	@Override
	public boolean isPreview() {
		return false;
	}
	
	@Override
	public Value getCompleteValue() {
		return getValue();
	}
	
	@Override
	public String toString() {
		return getTag().toString() + " = " + getValue().toString();
	}
}
