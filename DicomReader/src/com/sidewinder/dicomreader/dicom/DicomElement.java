package com.sidewinder.dicomreader.dicom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sidewinder.dicomreader.dicom.tag.Tag;
import com.sidewinder.dicomreader.dicom.value.Value;

public class DicomElement {

	private Tag tag;
	private boolean isPreview;
	private List<Value> values;
	
	// Element position and length in the DICOM file,
	// used to retrieve the complete content of the
	// current DICOM element should this be a preview
	private int elementPosition;
	private int elementLength;
	
	private DicomElement(Tag tag, List<Value> values,
			int elementPosition, int elementLength, boolean isPreview) {
		
		this.tag = tag;
		this.values = values;
		this.elementPosition = elementPosition;
		this.elementLength = elementLength;
		this.isPreview = isPreview;
	}
	
	public static DicomElement createSequenceDicomElement(Tag tag,
			List<Value> values, int elementPosition, int elementLength) {
		
		return new DicomElement(tag, values, elementPosition,
				elementLength, false);
	}
	
	public static DicomElement createPixelDataElement(Tag tag,
			List<Value> values, int elementPosition) {
		return new DicomElement(tag, values, elementPosition, -1, false);
	}
	
	public static DicomElement createDicomElement(Tag tag, int type,
			byte[] data,int elementPosition, int elementLength,
			boolean isPreview) {
		DicomElement dicomElement = null;
		List<Value> values;
		
		if (isPreview) {
			values = new ArrayList<Value>();
			values.add(Value.createValue(type, data,
					DicomFile.MAX_CACHED_BYTES));
		} else {
			values = readValues(type, data, elementLength);
		}
		
		dicomElement = new DicomElement(tag, values, elementPosition,
				elementLength, isPreview);
		
		return dicomElement;
	}
	
	private static List<Value> readValues(int type, byte[] data,
			int elementLength) {
		byte[] temp = new byte[elementLength];
		List<Value> values = new ArrayList<Value>();
		
		if (Value.isFixedLength(type)) {
			int valueLength = Value.getDicomLength(type);
			
			for (int iData = 0, iTemp = 0; iData < elementLength; iData++) {
				temp[iTemp++] = data[iData];
				if (iTemp == valueLength) {
					values.add(Value.createValue(type, temp, iTemp));
					iTemp = 0;
				}
			}
		} else {
			for (int iData = 0, iTemp = 0; iData < elementLength; iData++) {
				if (data[iData] == '\\') {
					values.add(Value.createValue(type, temp, iTemp));
					iTemp = 0;
				} else if (iData == elementLength - 1) {
					temp[iTemp++] = data[iData];
					values.add(Value.createValue(type, temp, iTemp));
				} else {
					temp[iTemp++] = data[iData];
				}
			}
		}
		
		return values;
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
		StringBuilder builder = new StringBuilder();
		
		builder.append(tag);
		builder.append(" = ");
		
		if (values.size() == 1) {
			builder.append(values.get(0));
		} else {
			builder.append(values);
		}
		
		return builder.toString();
	}
}
