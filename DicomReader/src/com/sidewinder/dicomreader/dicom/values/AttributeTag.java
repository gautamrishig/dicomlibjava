package com.sidewinder.dicomreader.dicom.values;

import com.sidewinder.dicomreader.dicom.tags.Tag;

// Just a wrapper for the Tag class
public class AttributeTag extends Value {
	
	protected AttributeTag(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
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
