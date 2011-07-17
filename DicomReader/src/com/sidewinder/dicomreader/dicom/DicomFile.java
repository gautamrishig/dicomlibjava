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

public class DicomFile {
	
	private static final int IMPLICIT_LENGTH = 0xFFFFFFFF & 0xFFFFFFFF;
	private static final int ZERO_LENGTH = 0x00000000 & 0xFFFFFFFF;
		
	protected static final int MAX_CACHED_BYTES = 128;
	
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
				elementPosition = currentPos;
				
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
						readBytes(temp128);
						skip(elementLength - MAX_CACHED_BYTES);
						dicomElement = DicomElement.createDicomElement(tag,
								type, temp128, elementPosition,
								elementLength, true);
					} else {
						// Complete
						readBytes(temp128, elementLength);
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
			readBytes(buffer);
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
	
	private int readContentLength(int type) throws IOException {
		byte[] temp2 = new byte[2];
		long elementLength;
		
		if (Value.has4BytesLength(type) || type == Tag.ITEM_TAG) {
			if (type != Tag.ITEM_TAG) {
				// Skipping the remaining bytes from of the
				// Value Representation word
				currentPos += bis.skip(2);
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
			readBytes(temp2);
			return DataMarshaller.getDicomUnsignedShort(temp2);
		}
	}
	
	private int readItemLength() throws IOException {
		byte[] temp4 = new byte[4];
		int elementLength;
		
		readBytes(temp4);
		elementLength = (int) DataMarshaller.getDicomUnsignedLong(temp4);
		
		return elementLength;
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
