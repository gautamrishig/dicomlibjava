package com.sidewinder.dicomreader.dicom.dicomelement;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public abstract class DicomElement {

	private Tag tag;
	private Value value;
	
	public static DicomElement createNormalDicomElement(Tag tag, Value value) {
		return new NormalDicomElement(tag, value);
	}
	
	public static DicomElement createPreviewDicomElement(Tag tag,
			Value value, long filePosition, long fullValueLength) {
		return new PreviewDicomElement(tag, value,
				filePosition, fullValueLength);
	}
	
	protected DicomElement(Tag tag, Value value) {
		this.tag = tag;
		this.value = value;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public Value getValue() {
		return value;
	}
	
	public abstract boolean isPreview();

	public abstract Value getCompleteValue();
}
