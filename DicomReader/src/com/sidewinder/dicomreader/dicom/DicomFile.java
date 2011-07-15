package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.SysexMessage;

import com.sidewinder.dicomreader.dicom.dicomelement.DicomElement;
import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;
import com.sidewinder.dicomreader.util.DataMarshaller;

public class DicomFile {
	
	private static final int IMPLICIT_LENGTH = 0xFFFFFFFF & 0xFFFFFFFF;
		
	private static final int MAX_CACHED_BYTES = 128;
	
	private DicomObject dicomObject;
	
	private File dicomFile;
	private boolean isExplicit;
	
	private int currentPos = 0;
	private FileInputStream is;
	private BufferedInputStream bis;
	
	public DicomFile(String file) {	
		dicomFile = new File(file);
		if (dicomFile.exists()) {
			readDicomFile(dicomFile);
		}
	}

	private void readDicomFile(File file) {
		DicomObject dicomObject;
		try {
			is = new FileInputStream(file);
			bis = new BufferedInputStream(is);
			// Skipping first 128 bytes and DICM string
			bis.skip(132);
			currentPos += 132;
			
			dicomObject = parseDicomObject(0);
			
			System.out.println("DicomObject:");
			System.out.println(dicomObject);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private DicomObject parseDicomObject(int dicomObjectLength)
			throws IOException {
		int type;
		long elementLength;
		long endingPos = Long.MAX_VALUE;
		Tag tag;
		Value value;
		DicomElement dicomElement = null;
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		
		// Computing DicomObjectLength (if caller gave this information)
		if (dicomObjectLength != 0) {
			endingPos = currentPos + dicomObjectLength;
		}
		
		while (bis.available() > 0 && currentPos < endingPos) {
			tag = readTag();
			
			// Skip if the tag is an ItemDelimitationTag or
			// SequenceDelimitationTag
			if (tag.isItemDelimitationTag() ||
					tag.isSequenceDelimitationTag()) {
				elementLength = readContentLength(Tag.ITEM_TAG);
			} else {
				type = readValueRepresentation();
				elementLength = readContentLength(type);
				
				if (type == Value.VR_SQ) {
					// Manage the container
					System.out.println("Container (" + currentPos + ", " + elementLength + ")");
					value = readSequenceValue(elementLength);
				} else {
					System.out.println("Normal Element (" + currentPos + ")");
					// Manage a normal element
					if (elementLength > MAX_CACHED_BYTES) {
						// Preview
						value = readElement(type, MAX_CACHED_BYTES);
						skip(elementLength - MAX_CACHED_BYTES);
						dicomElement = DicomElement.createPreviewDicomElement(
								tag, value, currentPos, elementLength);
					} else {
						// Complete
						value = readElement(type, elementLength);
						dicomElement =
							DicomElement.createNormalDicomElement(tag, value);
					}
				}
				
				dicomElementList.add(dicomElement);
			}
		}
		
		return new DicomObject(dicomElementList);
	}
	
	private Value readSequenceValue(long elementLength) 
			throws IOException {
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		long dicomObjectLength;
		
		long endingPos = currentPos + elementLength;
		Tag tag;
		
		while (currentPos < endingPos) {
			
			// Read ItemTag
			tag = readTag();
			if (!tag.isItemTag()) {
				//TODO: generate an error here!!!
				System.out.println("Error");
			}
			
			dicomObjectLength = readContentLength(Tag.ITEM_TAG);
			dicomObjectList.add(parseDicomObject((int) dicomObjectLength));
		}
		
		return Value.createContainerValue(dicomObjectList, elementLength);
	}
	
	private Value readElement(int type, long elementLength) 
			throws IOException {
		byte[] temp128 = new byte[MAX_CACHED_BYTES];
		
		readBytes(temp128, (int) elementLength);
		
		return Value.createValue(type, temp128, elementLength);
	}
	
	private long readContentLength(int type) throws IOException {
		byte[] temp2 = new byte[2];
		byte[] temp4 = new byte[4];
		long elementLength;
		
		if (Value.has4BytesLength(type) || type == Tag.ITEM_TAG) {
			if (type != Tag.ITEM_TAG) {
				// Skipping the remaining bytes from of the
				// Value Representation word
				currentPos += bis.skip(2);
			}
			// Reading element length
			readBytes(temp4);
			elementLength = DataMarshaller.getDicomUnsignedLong(temp4);
			// Check if Length is implicit. If so, compute the element's length
			if (elementLength == IMPLICIT_LENGTH) {
				elementLength = computeLength(type);
			}
			return elementLength;
		} else {
			readBytes(temp2);
			return DataMarshaller.getDicomUnsignedShort(temp2);
		}
	}
	
	private int readValueRepresentation() throws IOException {
		byte[] temp2 = new byte[2];
		
		readBytes(temp2);
		return Value.getVRIdentifier(new String(temp2));
	}
	
	private Tag readTag() throws IOException {
		byte[] temp4 = new byte[4];
		
		readBytes(temp4);
		return new Tag(temp4);
	}
	
	private void readBytes(byte[] buffer) throws IOException {
		currentPos += bis.read(buffer);
	}
	
	private void skip(long toSkip) throws IOException {
		long skipped;
		
		while (toSkip > 0) {
			skipped = bis.skip(toSkip);
			if (skipped < 0) {
				throw new IOException("Error skipping bytes.");
			}
			
			toSkip -= skipped;
		}
	}

	private int computeLength(int type) throws IOException {
		int length = 0;
		
		if (type == Value.VR_SQ) {
			length = computeSequenceLength();
		} else {
			length = computeDicomObjectLength();
		}
		
		// Accounting for the ending tag and fake length
		length += 4;
		
		// Restoring the last saved position
		is.getChannel().position(currentPos);
		bis = new BufferedInputStream(is);
		
		return length;
	}
	
	private int computeSequenceLength() throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += bis.read(temp4);
		} while (!new Tag(temp4).isSequenceDelimitationTag());
		
		return length;
	}
	
	private int computeDicomObjectLength() throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += bis.read(temp4);
		} while (!new Tag(temp4).isItemDelimitationTag());
		
		return length;
	}

	private void readBytes(byte[] buffer, int length)
			throws IOException {
		currentPos += bis.read(buffer, 0, length);
	}
	
}
