package com.sidewinder.dicomreader.dicom.dicomelement;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public class PreviewDicomElement extends DicomElement {

	private long filePosition;
	private long fullValueLength;
	private Value fullValue = null;
	
	protected PreviewDicomElement(Tag tag, Value value,
			long filePosition, long fullValueLength) {
		super(tag, value);
		this.filePosition = filePosition;
		this.fullValueLength = fullValueLength;
	}
	
	@Override
	public boolean isPreview() {
		return true;
	}

	@Override
	public Value getCompleteValue() {
		if (fullValue == null) {
			return null; //TODO: retrieve the full value here!
		} else {
			return fullValue;
		}
	}

	@Override
	public String toString() {
		return getTag().toString() + " = " + getValue().toString() + " (preview)";
	}
}
