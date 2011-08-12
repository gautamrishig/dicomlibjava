package com.sidewinder.dicomlib.dicom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sidewinder.dicomlib.dicom.tag.Tag;
import com.sidewinder.dicomlib.dicom.value.Value;
import com.sidewinder.dicomlib.exception.MalformedDicomException;
import com.sidewinder.dicomlib.util.DataMarshaller;
import com.sidewinder.dicomlib.util.ImageVisualizer;
import com.sidewinder.dicomlib.util.PositionalInputStream;

public class DicomFileReader {
	
	private static final int IMPLICIT_LENGTH = 0xFFFFFFFF & 0xFFFFFFFF;
	private static final int ZERO_LENGTH = 0x00000000 & 0xFFFFFFFF;
		
	protected static final int MAX_CACHED_BYTES = 128;

	public static DicomFileContext readDicomFile(String file)
			throws MalformedDicomException {
		File dicomFile;
		DicomFileContext context = null;
		
		dicomFile = new File(file);
		if (dicomFile.exists()) {
			try {
				context = new DicomFileContext(dicomFile);
				
				// Skipping first 128 bytes and DICM string
				context.getPis().skip(132);
				
				// Parsing the file
				context.setDicomObject(parseDicomObject(0, context));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		return context;
	}
	
	private static DicomObject parseDicomObject(int dicomObjectLength,
			DicomFileContext context) throws IOException, MalformedDicomException{
		int type;
		int elementPosition;
		int elementLength;
		long endingPos = Long.MAX_VALUE;
		Tag tag;
		byte[] temp128 = new byte[MAX_CACHED_BYTES];
		List<Value> values;
		DicomElement dicomElement = null;
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		PositionalInputStream pis = context.getPis();
		
		// Computing DicomObjectLength (if caller gave this information)
		if (dicomObjectLength != 0) {
			endingPos = pis.getPosition() + dicomObjectLength;
		}
		
		while (pis.available() > 0 && pis.getPosition() < endingPos) {
			elementPosition = pis.getPosition();
			tag = readTag(context);
			System.out.println(tag);
			
			if (tag.isPixelDataTag()) {
				type = readValueRepresentation(context);
				pis.skip(2);
				elementLength = readItemLength(context);
				elementPosition = pis.getPosition();
				dicomElement = DicomElement.createPixelDataElement(tag,
						readPixelData(type, elementLength, context), elementPosition);
				dicomElementList.add(dicomElement);
			} else if (tag.isItemDelimitationTag() ||
					tag.isSequenceDelimitationTag()) {
				// Skip if the tag is an ItemDelimitationTag or
				// SequenceDelimitationTag
				elementLength = readContentLength(Tag.ITEM_TAG, context);
			} else {
				type = readValueRepresentation(context);
				elementLength = readContentLength(type, context);
				
				if (type == Value.SQ) {
					// Manage the container
					values = new ArrayList<Value>();
					values.add(readSequenceValue(elementLength, context));
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
	
	private static List<Value> readPixelData(int type, int elementLength,
			DicomFileContext context) throws IOException, MalformedDicomException {
		byte[] buffer;
		List<Value> values = new ArrayList<Value>();
		Tag tag;
		int length;
		int boundary;
		int[] basicOffsetTable;
		
		if (elementLength == IMPLICIT_LENGTH) {
			// Encapsulated value
			
			// Reading Basic Offset Table Item
			tag = readTag(context);
			if (!tag.isItemTag()) {
				//TODO: throw an error here!
			}
			length = readItemLength(context);
			basicOffsetTable = readBasicOffsetTable(length, context);
			
			// Read frames
			for (int i = 0; i < basicOffsetTable.length; i++) {
				if (i == basicOffsetTable.length - 1) {
					boundary = Integer.MAX_VALUE;
				} else {
					boundary = basicOffsetTable[i+1];
				}
				
				values.add(readFrame(type, boundary, context));
			}
			
		} else {
			buffer = new byte[elementLength];
			// Native value
			context.getPis().read(buffer);
			values.add(Value.createValue(type, buffer, elementLength));
		}
		
		return values;
	}
	
	private static Value readFrame(int type, int boundary,
			DicomFileContext context)
			throws IOException, MalformedDicomException {
		byte[] buffer = null;
		byte[] tempBuffer = null;
		Tag tag;
		int totalLength = 0;
		int length;
		PositionalInputStream pis = context.getPis();
		
		do {
			tag = readTag(context);
			if (tag.isSequenceDelimitationTag()) {
				length = readItemLength(context);
				break;
			}
			
			if (!tag.isItemTag()) {
				throw new MalformedDicomException("Item Tag expected " +
						" while reading Frame at position " + pis.getPosition()); 
			}
			
			length = readItemLength(context);
			buffer = new byte[length];
			if (tempBuffer != null) {
				tempBuffer = buffer.clone();
				buffer = new byte[totalLength + length];
				System.arraycopy(tempBuffer, 0, buffer,
						0, tempBuffer.length);
			}
			pis.read(buffer, totalLength, length);
			totalLength += length;
			
		} while (pis.getPosition() < boundary);
		
		if (buffer == null) {
			throw new MalformedDicomException("No Pixel Data found.");
		}
		
		return Value.createValue(type, buffer, totalLength);
	}
	
	private static int[] readBasicOffsetTable(int tableLength,
			DicomFileContext context) throws IOException {
		int[] basicOffsetTable;
		int elementsInTable;
		int imageStart;
		
		if (tableLength % 4 != 0) {
			//TODO: Throw a DICOM MALFORMED exception
		}
		
		elementsInTable = tableLength / 4;
		
		if (tableLength == ZERO_LENGTH) {
			basicOffsetTable = new int[1];
			basicOffsetTable[0] = 0;
		} else {
			basicOffsetTable = new int[elementsInTable];
			for (int i = 0; i < elementsInTable; i++) {
				basicOffsetTable[i] = readItemLength(context);
			}
		}
		
		imageStart = context.getPis().getPosition();
		for (int i = 0; i < basicOffsetTable.length; i++) {
			basicOffsetTable[i] += imageStart; 
		}
		
		return basicOffsetTable;
	}
	
	private static Value readSequenceValue(int elementLength, DicomFileContext context) 
			throws IOException, MalformedDicomException {
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		int dicomObjectLength;
		PositionalInputStream pis = context.getPis();
		int endingPos = pis.getPosition() + elementLength;
		Tag tag;
		
		while (pis.getPosition() < endingPos) {
			
			// Read ItemTag
			tag = readTag(context);
			if (!tag.isItemTag()) {
				//TODO: Throw a DICOM MALFORMED exception
			}
			
			dicomObjectLength = readContentLength(Tag.ITEM_TAG, context);
			dicomObjectList.add(parseDicomObject(dicomObjectLength, context));
		}
		
		return Value.createContainerValue(dicomObjectList, elementLength);
	}
	
	private static int readContentLength(int type, DicomFileContext context)
			throws IOException {
		byte[] temp2 = new byte[2];
		long elementLength;
		PositionalInputStream pis = context.getPis();
		
		if (Value.has4BytesLength(type) || type == Tag.ITEM_TAG) {
			if (type != Tag.ITEM_TAG) {
				// Skipping the remaining bytes from of the
				// Value Representation word
				pis.skip(2);
			}
			// Reading element length
			elementLength = readItemLength(context);
			// Check if Length is implicit. If so, compute the element's length
			if (elementLength == IMPLICIT_LENGTH) {
				elementLength = computeLength(type, context);
			}
			//TODO: we assume 2^32/2 is the maximum length for any of the elements
			return (int)elementLength;
		} else {
			pis.read(temp2);
			return DataMarshaller.getDicomUnsignedShort(temp2);
		}
	}
	
	private static int readItemLength(DicomFileContext context)
			throws IOException {
		byte[] temp4 = new byte[4];
		int elementLength;
		
		context.getPis().read(temp4);
		elementLength = (int) DataMarshaller.getDicomUnsignedLong(temp4);
		
		return elementLength;
	}
	
	private static int readValueRepresentation(DicomFileContext context)
			throws IOException {
		byte[] temp2 = new byte[2];
		
		context.getPis().read(temp2);
		return Value.getVRIdentifier(new String(temp2));
	}
	
	private static Tag readTag(DicomFileContext context) throws IOException {
		byte[] temp4 = new byte[4];
		
		context.getPis().read(temp4);
		return new Tag(temp4);
	}

	private static int computeLength(int type, DicomFileContext context)
			throws IOException {
		int length = 0;
		PositionalInputStream pis = context.getPis();
		int storedPosition = pis.getPosition();
		
		if (type == Value.SQ) {
			length = computeSequenceLength(context);
		} else {
			length = computeDicomObjectLength(context);
		}
		
		// Accounting for the ending tag and 0x00000000 length
		length += 4;
		
		// Restoring the last saved position
		pis.position(storedPosition);
		
		return length;
	}
	
	private static int computeSequenceLength(DicomFileContext context)
			throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += context.getPis().read(temp4);
		} while (!new Tag(temp4).isSequenceDelimitationTag());
		
		return length;
	}
	
	private static int computeDicomObjectLength(DicomFileContext context)
			throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += context.getPis().read(temp4);
		} while (!new Tag(temp4).isItemDelimitationTag());
		
		return length;
	}
	
}
