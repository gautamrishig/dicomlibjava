package com.sidewinder.dicomreader.dicom;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public class NormalDicomElement {
	
	private Tag tag;
	private Value value;
	
	public NormalDicomElement(Tag tag, Value value) {
		this.tag = tag;
		this.value = value;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public Value getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return tag.toString() + " = " + value.toString();
	}
}