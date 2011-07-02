package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sidewinder.dicomreader.dicom.dicomelement.DicomElement;
import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;
import com.sidewinder.dicomreader.util.DataMarshaller;

public class Dicom {
	
	private static final int IMPLICIT_LENGTH = 0xFFFFFFFF & 0xFFFFFFFF;
	
	private static final int MAX_CACHED_BYTES = 128;
	
	private DicomObject dicomObject;
	
	private File dicomFile;
	private boolean isExplicit;
	
	private int currentPos = 0;
	private FileInputStream is;
	private BufferedInputStream bis;
	
	public Dicom(String file) {	
		dicomFile = new File(file);
		if (dicomFile.exists()) {
			readDicomFile(dicomFile);
		}
	}

	private void readDicomFile(File file) {
		try {
			is = new FileInputStream(file);
			bis = new BufferedInputStream(is);
			// Skipping first 128 bytes and DICM string
			bis.skip(132);
			currentPos += 132;

			// Old version...
			//List<DicomObject> list = parseExplicit(-1, 0);
			
			parseDicomObject(0);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private DicomObject parseDicomObject(int dicomObjectLength) throws IOException {
		int type;
		long elementLength;
		Tag tag;
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		
		// Read tag (4 bytes)
		tag = readTag();
		
		while (bis.available() > 0) {
			type = readValueRepresentation();
			elementLength = readContentLength(type);
			
			if (type == Value.VR_SQ) {
				// Manage the container
			} else {
				// Manage a normal element
				if (elementLength > MAX_CACHED_BYTES) {
					// Preview
				} else {
					// Complete
				}
			}
		}
		
		return new DicomObject(dicomElementList);
	}
	
	private Value readElement(int type, long elementLength) 
			throws IOException {
		byte[] temp128 = new byte[MAX_CACHED_BYTES];
		
		
		
		return null;
	}
	
	private long readContentLength(int type) throws IOException {
		byte[] temp2 = new byte[2];
		byte[] temp4 = new byte[4];
		long elementLength;
		
		if (Value.has4BytesLength(type)) {
			// Skipping the remaining bytes from of the
			// Value Representation word
			currentPos += bis.skip(2);
			// Reading element length
			readBytes(temp4);
			elementLength = DataMarshaller.getDicomUnsignedLong(temp4);
			// Check if Length is implicit. If so, compute the element's length
			if (elementLength == IMPLICIT_LENGTH) {
				elementLength = computeLength();
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private List<DicomObject> parseExplicit(long maxBytesToRead,
			long startingByte) throws IOException {
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		byte[] temp2 = new byte[2];
		byte[] temp4 = new byte[4];
		byte[] temp128 = new byte[128];
		
		int vr;
		int dicomElementLength;
		
		Tag tag;
		Value value = null;
		DicomElement dicomElement = null;
		
		while (bis.available() > 0 &&
				currentPos - startingByte != maxBytesToRead) {
			// Reading DICOM Tag and Value Representation info
			readBytes(temp4);
			tag = new Tag(temp4);
			
			// Create a new DicomObject and add it to the list, plus reset
			// the dicomElementList object
			if (tag.isDicomObjectStart()) {
				System.out.println(currentPos + " Creating new DicomObject");
				dicomObjectList.add(new DicomObject(dicomElementList));
				dicomElementList = new ArrayList<DicomElement>();
				
				// Skip residual element length and read new tag
				readBytes(temp4);
				readBytes(temp4);
				tag = new Tag(temp4);
			} else if (tag.isDicomElementEnd()) {
				System.out.println("END"); //TODO: Debug, remove it!!
				continue;
			}

			// Reading Value Representation
			readBytes(temp2);
			vr = Value.getVRIdentifier(new String(temp2));
			
			// Reading and storing data
			if (Value.hasLongContent(vr)) {
				
				dicomElementLength = getLongContentLength(vr);
				
				if (dicomElementLength == IMPLICIT_LENGTH) {
					// Reading start tag
					readBytes(temp4); // TODO: Now it's ignored, should I do something with it? Like a sanity check?
					dicomElementLength = computeLength();
				}
				
				if (Value.isContainerElement(vr)) {
					System.out.println("Container (" + currentPos + ")");
					// Extracting the Delimitation Item
					readBytes(temp4); // TODO: Now it's ignored, should I do something with it? Like a sanity check? 
					// Getting residual SQ length
					readBytes(temp4); //TODO: this is only the length of the single dicomobject, not the whole SQ element!!!!
					dicomElementLength = (int) DataMarshaller.getDicomUnsignedLong(temp4);
					System.out.println("\tContainer length: " + dicomElementLength);
					List<DicomObject> sqContent = parseExplicit(
							dicomElementLength, currentPos);
					value = Value.createContainerValue(sqContent, dicomElementLength);
					dicomElement = DicomElement.createNormalDicomElement(tag, value);
					continue;
				} else if (dicomElementLength > 128) {
					bis.read(temp128);
					value = Value.createValue(vr, temp128, 128);
					long skipped = bis.skip(dicomElementLength - 128);
					if (skipped != dicomElementLength - 128) {
						System.out.println("Skip problem!"); //TODO: debug, remove it!
						is.getChannel().position(currentPos + dicomElementLength);
						bis = new BufferedInputStream(is);
					}
					// Composing DICOM Element
					System.out.println("preview");
					dicomElement = DicomElement.createPreviewDicomElement(tag,
							value, currentPos, dicomElementLength);
					currentPos += dicomElementLength;
				} else {
					readBytes(temp128, dicomElementLength);
					value = Value.createValue(vr, temp128, dicomElementLength);
					// Composing DICOM Element
					dicomElement = 
						DicomElement.createNormalDicomElement(tag, value);
				}
				
			} else {
				currentPos += bis.read(temp2);
				// Reading the length of the element
				dicomElementLength = 
					DataMarshaller.getDicomUnsignedShort(temp2);
				readBytes(temp128, dicomElementLength);
				value = Value.createValue(vr, temp128, dicomElementLength);
				// Composing DICOM Element
				dicomElement = 
					DicomElement.createNormalDicomElement(tag, value);
			}
			
			//System.out.println(dicomElement);
			dicomElementList.add(dicomElement);
		}
		
		System.out.println("Exiting container (" + currentPos + ")");
		dicomObjectList.add(new DicomObject(dicomElementList));
		return dicomObjectList;
	}
	
	private void readBytes(byte[] buffer, int length)
			throws IOException {
		currentPos += bis.read(buffer, 0, length);
	}
	
	private int computeLength() throws IOException {
		byte[] temp4 = new byte[4];
		int length = 0;
		
		do {
			length += bis.read(temp4);
		} while (!new Tag(temp4).isDicomElementEnd());
		
		// Not accounting for the ending tag
		length -= 4;
		
		// Restoring the last saved position
		is.getChannel().position(currentPos);
		bis = new BufferedInputStream(is);
		
		return length;
	}

	private int getLongContentLength(int type)
			throws IOException {
		byte[] temp2 = new byte[2];
		byte[] temp4 = new byte[4];
		
		if (Value.has4BytesLength(type)) {
			bis.skip(2);
			currentPos += 2;
			// Reading the length of the element (4 bytes length)
			currentPos += bis.read(temp4);
			return (int) DataMarshaller.getDicomUnsignedLong(temp4);
		} else {
			// Reading the length of the element (2 bytes length)
			currentPos += bis.read(temp2);
			return DataMarshaller.getDicomUnsignedShort(temp2);
		}
	}
}
