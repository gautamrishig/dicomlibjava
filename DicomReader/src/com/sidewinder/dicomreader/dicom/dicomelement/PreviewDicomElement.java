package com.sidewinder.dicomreader.dicom.dicomelement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;

public class PreviewDicomElement extends DicomElement {

	private long valuePosition;
	private long fullValueLength;
	private Value fullValue = null;
	
	protected PreviewDicomElement(Tag tag, Value value,
			long filePosition, long fullValueLength) {
		super(tag, value);
		this.valuePosition = filePosition;
		this.fullValueLength = fullValueLength;
	}
	
	@Override
	public boolean isPreview() {
		return true;
	}

	@Override
	public Value getCompleteValue(String fileName)
			throws IllegalArgumentException {
		byte[] temp = new byte[(int)fullValueLength];
		
		if (fullValue == null) {
			return null;
		} else {
			try {
				RandomAccessFile file = new RandomAccessFile(fileName, "ro");
				file.seek(valuePosition);
				file.read(temp);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("DICOM file " + 
						fileName + " not found");
			} catch (IOException e) {
				throw new IllegalArgumentException("Error reading DICOM " +
						fileName + " file");
			}
			
			return Value.createValue(this.getValue().getType(), temp,
					temp.length);
		}
	}

	@Override
	public String toString() {
		return getTag().toString() + " = " + getValue().toString() + " (preview)";
	}
}
