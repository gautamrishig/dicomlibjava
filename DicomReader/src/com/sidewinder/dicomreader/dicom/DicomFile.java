package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.values.Value;
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
		int elementPos;
		int elementLength;
		long endingPos = Long.MAX_VALUE;
		Tag tag;
		List<Value> values;
		DicomElement dicomElement = null;
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		
		// Computing DicomObjectLength (if caller gave this information)
		if (dicomObjectLength != 0) {
			endingPos = currentPos + dicomObjectLength;
		}
		
		while (bis.available() > 0 && currentPos < endingPos) {
			tag = readTag();
			
			if (tag.isPixelDataTag()) {
				System.out.println("Pixel Data (ignoring");
				break;
			}
			
			// Skip if the tag is an ItemDelimitationTag or
			// SequenceDelimitationTag
			if (tag.isItemDelimitationTag() ||
					tag.isSequenceDelimitationTag()) {
				elementLength = readContentLength(Tag.ITEM_TAG);
			} else {
				type = readValueRepresentation();
				elementLength = readContentLength(type);
				elementPos = currentPos;
				
				if (type == Value.SQ) {
					// Manage the container
					values = new ArrayList<Value>();
					values.add(readSequenceValue(elementLength));
					dicomElement = new DicomElement(tag, values, elementPos, elementLength, false);
				} else {
					// Manage a normal element
					if (elementLength > MAX_CACHED_BYTES) {
						// Preview
						values = new ArrayList<Value>();
						values.add(readPreviewValue(type));
						skip(elementLength - MAX_CACHED_BYTES);
						dicomElement = new DicomElement(tag, values,
								elementPos, elementLength, true);
						
					} else {
						// Complete
						values = readValues(type, elementLength);
						dicomElement = new DicomElement(tag, values, elementPos, elementLength, false);
					}
				}
				
				dicomElementList.add(dicomElement);
			}
		}
		
		return new DicomObject(dicomElementList);
	}
	
	private Value readPixelData(int type, int elementLength) 
			throws IOException {
		byte[] buffer = new byte[elementLength];
		Value value;
		
		if (elementLength == IMPLICIT_LENGTH) {
			// Encapsulated value
			
			// Reading Basic Offset Table Item
			
			value = null;
		} else {
			// Native value
			readBytes(buffer);
			value = Value.createValue(type, buffer, elementLength);
		}
		
		return value;
	}
	
	private Value readSequenceValue(int elementLength) 
			throws IOException {
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		int dicomObjectLength;
		int endingPos = currentPos + elementLength;
		Tag tag;
		
		while (currentPos < endingPos) {
			
			// Read ItemTag
			tag = readTag();
			if (!tag.isItemTag()) {
				//TODO: generate an error here!!!
				System.out.println("Error");
			}
			
			dicomObjectLength = readContentLength(Tag.ITEM_TAG);
			dicomObjectList.add(parseDicomObject(dicomObjectLength));
		}
		
		return Value.createContainerValue(dicomObjectList, elementLength);
	}
	
	private Value readPreviewValue(int type)
			throws IOException {
		byte[] temp128 = new byte[MAX_CACHED_BYTES];
		
		readBytes(temp128);
		
		return Value.createValue(type, temp128, MAX_CACHED_BYTES);
	}
	
	private List<Value> readValues(int type, int elementLength) 
			throws IOException {
		byte[] tempA = new byte[elementLength];
		byte[] tempB = new byte[elementLength];
		List<Value> values = new ArrayList<Value>();
		
		readBytes(tempA, elementLength);
		
		if (Value.isFixedLength(type)) {
			int valueLength = Value.getDicomLength(type);
			
			for (int iA = 0, iB = 0; iA < elementLength; iA++) {
				tempB[iB++] = tempA[iA];
				if (iB == valueLength) {
					values.add(Value.createValue(type, tempB, iB));
					iB = 0;
				}
			}
		} else {
			for (int iA = 0, iB = 0; iA < elementLength; iA++) {
				if (tempA[iA] == '\\') {
					values.add(Value.createValue(type, tempB, iB));
					iB = 0;
				} else if (iA == elementLength - 1) {
					tempB[iB++] = tempA[iA];
					values.add(Value.createValue(type, tempB, iB));
				} else {
					tempB[iB++] = tempA[iA];
				}
			}
		}
		
		return values;
	}
	
	private int readContentLength(int type) throws IOException {
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
			//TODO: we assume 2^32/2 is the maximum length for any of the elements
			return (int)elementLength;
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
		
		if (type == Value.SQ) {
			length = computeSequenceLength();
		} else {
			length = computeDicomObjectLength();
		}
		
		// Accounting for the ending tag and 0x00000000 length
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
