package com.sidewinder.dicomreader.dicom.value;

import com.sidewinder.dicomreader.dicom.tag.Tag;

// Just a wrapper for the Tag class
public class AttributeTagValue extends Value {
	
	protected AttributeTagValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		
		if (contentLength != getDicomLength() ||
				data.length < getDicomLength()) {
			throw new IllegalArgumentException("Value Representation " +
					getType() + " must be " + getDicomLength() + 
					" bytes long.");
		}
		
		return new Tag(data);
	}

	@Override
	protected String getStringValue() {
		return getValue().toString();
	}

}
