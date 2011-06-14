package com.sidewinder.dicomreader.dicom;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public class DicomElement<E> {

	private Tag tag;
	private Value<E> value;
	
	private DicomElement(Tag tag, Value<E> value) {
		this.tag = tag;
		this.value = value;
	}
	
	public <T> DicomElement<T> createDicomElement(Tag tag, Value<T> value) {
		return null;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public Value<E> getValue() {
		return value;
	}
}
