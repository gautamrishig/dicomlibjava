package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sidewinder.dicomreader.dicom.tag.Tag;
import com.sidewinder.dicomreader.dicom.value.Value;
import com.sidewinder.dicomreader.util.DataMarshaller;
import com.sidewinder.dicomreader.util.PositionalInputStream;

public class DicomFile {
	
	private static final int IMPLICIT_LENGTH = 0xFFFFFFFF & 0xFFFFFFFF;
	private static final int ZERO_LENGTH = 0x00000000 & 0xFFFFFFFF;
		
	protected static final int MAX_CACHED_BYTES = 128;
	
	private DicomObject dicomObject;
	
	private File dicomFile;
	private boolean isExplicit;
	
	private PositionalInputStream pis;
	
	public DicomFile(String file) {	
		dicomFile = new File(file);
		if (dicomFile.exists()) {
			readDicomFile(dicomFile);
		}
	}

	private void readDicomFile(File file) {
		DicomObject dicomObject;
		try {
			pis = new PositionalInputStream(file);
			// Skipping first 128 bytes and DICM string
			pis.skip(132);
			
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
		int elementPosition;
		int elementLength;
		long endingPos = Long.MAX_VALUE;
		Tag tag;
		byte[] temp128 = new byte[MAX_CACHED_BYTES];
		List<Value> values;
		DicomElement dicomElement = null;
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		
		// Computing DicomObjectLength (if caller gave this information)
		if (dicomObjectLength != 0) {
			endingPos = pis.getPosition() + dicomObjectLength;
		}
		
		while (pis.available() > 0 && pis.getPosition() < endingPos) {
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
				elementPosition = pis.getPosition();
				
				if (type == Value.SQ) {
					// Manage the container
					values = new ArrayList<Value>();
					values.add(readSequenceValue(elementLength));
					dicomElement = DicomElement.createSequenceDicomElement(tag,
							values, elementPosition, elementLength);
				} else {
					// Manage a normal element
					if (elementLength > MAX_CACHED_BYTES) {
						// Preview
						values = new ArrayList<Value>();
						pis.read(temp128);
						pis.skip(elementLength - MAX_CACHED_BYTES);
						dicomElement = DicomElement.createDicomElement(tag,
								type, temp128, elementPosition,
								elementLength, true);
					} else {
						// Complete
						pis.read(temp128, elementLength);
						dicomElement = DicomElement.createDicomElement(tag,
								type, temp128, elementPosition,
								elementLength, false);
					}
				}
				
				dicomElementList.add(dicomElement);
			}
		}
		
		return new DicomObject(dicomElementList);
	}
	
	private List<Value> readPixelData(int type, int elementLength) 
			throws IOException {
		byte[] buffer = new byte[elementLength];
		List<Value> values = new ArrayList<Value>();
		Tag tag;
		int length;
		int[] basicOffsetTable;
		
		if (elementLength == IMPLICIT_LENGTH) {
			// Encapsulated value
			
			// Reading Basic Offset Table Item
			tag = readTag();
			if (!tag.isItemTag()) {
				//TODO: throw an error here!
			}
			length = readItemLength();
			basicOffsetTable = readBasicOffsetTable(length);
			
			// Read frames
		} else {
			// Native value
			pis.read(buffer);
			values.add(Value.createValue(type, buffer, elementLength));
		}
		
		return values;
	}
	
	private int[] readBasicOffsetTable(int tableLength) throws IOException {
		int[] basicOffsetTable;
		int elementsInTable;
		
		if (tableLength % 4 != 0) {
			//TODO: Throw a DICOM MALFORMED exception
		}
		
		elementsInTable = tableLength / 4;
		
		if (tableLength == ZERO_LENGTH) {
			basicOffsetTable = new int[1];
			basicOffsetTable[0] = 0;
		} else {
			basicOffsetTable = new int[elementsInTable];
			for (int i = 0; i < tableLength; i++) {
				basicOffsetTable[i] = readItemLength();
			}
		}
		
		return basicOffsetTable;
	}
	
	private Value readSequenceValue(int elementLength) 
			throws IOException {
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		int dicomObjectLength;
		int endingPos = pis.getPosition() + elementLength;
		Tag tag;
		
		while (pis.getPosition() < endingPos) {
			
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
	
	private int readContentLength(int type) throws IOException {
		byte[] temp2 = new byte[2];
		long elementLength;
		
		if (Value.has4BytesLength(type) || type == Tag.ITEM_TAG) {
			if (type != Tag.ITEM_TAG) {
				// Skipping the remaining bytes from of the
				// Value Representation word
				pis.skip(2);
			}
			// Reading element length
			elementLength = readItemLength();
			// Check if Length is implicit. If so, compute the element's length
			if (elementLength == IMPLICIT_LENGTH) {
				elementLength = computeLength(type);
			}
			//TODO: we assume 2^32/2 is the maximum length for any of the elements
			return (int)elementLength;
		} else {
			pis.read(temp2);
			return DataMarshaller.getDicomUnsignedShort(temp2);
		}
	}
	
	private int readItemLength() throws IOException {
		byte[] temp4 = new byte[4];
		int elementLength;
		
		pis.read(temp4);
		elementLength = (int) DataMarshaller.getDicomUnsignedLong(temp4);
		
		return elementLength;
	}
	
	private int readValueRepresentation() throws IOException {
		byte[] temp2 = new byte[2];
		
		pis.read(temp2);
		return Value.getVRIdentifier(new String(temp2));
	}
	
	private Tag readTag() throws IOException {
		byte[] temp4 = new byte[4];
		
		pis.read(temp4);
		return new Tag(temp4);
	}

	private int computeLength(int type) throws IOException {
		int length = 0;
		int storedPosition = pis.getPosition();
		
		if (type == Value.SQ) {
			length = computeSequenceLength();
		} else {
			length = computeDicomObjectLength();
		}
		
		// Accounting for the ending tag and 0x00000000 length
		length += 4;
		
		// Restoring the last saved position
		pis.position(storedPosition);
		
		return length;
	}
	
	private int computeSequenceLength() throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += pis.read(temp4);
		} while (!new Tag(temp4).isSequenceDelimitationTag());
		
		return length;
	}
	
	private int computeDicomObjectLength() throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += pis.read(temp4);
		} while (!new Tag(temp4).isItemDelimitationTag());
		
		return length;
	}
	
}
