package com.sidewinder.dicomreader.dicom;

import java.util.List;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.values.Value;

public class DicomElement {

	private Tag tag;
	private boolean isPreview;
	private List<Value> values;
	
	// Element position and length in the DICOM file,
	// used to retrieve the complete content of the
	// current DICOM element should this be a preview
	private int elementPosition;
	private int elementLength;
	
	public DicomElement(Tag tag, List<Value> values,
			int elementPosition, int elementLength, boolean isPreview) {
		this.tag = tag;
		this.values = values;
		this.elementPosition = elementPosition;
		this.elementLength = elementLength;
		this.isPreview = isPreview;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public List<Value> getValue() {
		return values;
	}
	
	public boolean isPreview() {
		return isPreview;
	}
	
	protected int getElementPosition() {
		return elementPosition;
	}
	
	protected int getElementLength() {
		return elementLength;
	}

	@Override
	public String toString() {
		//TODO: broken! iterate over all the stored values
		return getTag().toString() + " = " + values.toString();
	}
}
