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
	
	private DicomObject dicomObject;
	
	private File dicomFile;
	private boolean isExplicit;
	
	public Dicom(String file) {	
		dicomFile = new File(file);
		if (dicomFile.exists()) {
			readDicomFile(dicomFile);
		}
	}

	private void readDicomFile(File file) {
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			// Skipping first 128 bytes and DICM string
			bis.skip(132);

			parseExplicit(bis, -1, 0); //TODO: Remember to use the return element!!!

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<DicomObject> parseExplicit(BufferedInputStream bis,
			long maxBytesToRead, long currentPos) throws IOException {
		List<DicomElement> dicomElementList = new ArrayList<DicomElement>();
		List<DicomObject> dicomObjectList = new ArrayList<DicomObject>();
		byte[] temp4 = new byte[4];
		byte[] temp128 = new byte[128];
		
		int vr;
		int dicomElementLength;
		long readBytes = 0;
		
		Tag tag;
		Value value = null;
		DicomElement dicomElement = null;
		
		while (bis.available() > 0 || readBytes == maxBytesToRead) {
			// Reading DICOM Tag and Value Representation info
			readBytes += bis.read(temp4);
			tag = new Tag(temp4);
			
			// Create a new DicomObject and add it to the list, plus reset
			// the dicomElementList object
			if (tag.isDicomObjectStart()) {
				dicomObjectList.add(new DicomObject(dicomElementList));
				dicomElementList = new ArrayList<DicomElement>();
				
				// Read residual element length and new tag
				readBytes += bis.read(temp4);
				readBytes += bis.read(temp4);
				tag = new Tag(temp4);
			}

			// Reading Value Representation
			readBytes += bis.read(temp4);
			vr = Value.getVRIdentifier(new String(temp4, 0, 2));
			
			// Reading and storing data
			if (Value.hasLongContent(vr)) {
				
				if (Value.has4BytesLength(vr)) {
					// Reading the length of the element (4 bytes length)
					readBytes += bis.read(temp4);
					dicomElementLength = 
						(int) DataMarshaller.getDicomUnsignedLong(temp4);
				} else {
					// Reading the length of the element (2 bytes length)
					dicomElementLength =
						DataMarshaller.getDicomUnsignedShort(temp4, 2);
				}
				
				if (Value.isContainerElement(vr)) {
					System.out.println("Container");
					// Extracting the Delimitation Item
					readBytes += bis.read(temp4); // TODO: Now it's ignored, should I do something with it? Like a sanity check? 
					// Getting residual SQ length
					readBytes += bis.read(temp4);
					dicomElementLength =
						(int) DataMarshaller.getDicomUnsignedLong(temp4);
					List<DicomObject> sqContent = parseExplicit(bis,
							dicomElementLength, readBytes);
					value = Value.createContainerValue(sqContent, dicomElementLength);
					dicomElement = DicomElement.createNormalDicomElement(tag, value);
				} else if (dicomElementLength > 128) {
					bis.read(temp128);
					value = Value.createValue(vr, temp128, 128);
					bis.skip(dicomElementLength - 128);
					// Composing DICOM Element
					System.out.println("preview");
					dicomElement = DicomElement.createPreviewDicomElement(tag,
							value, currentPos + readBytes, dicomElementLength);
					readBytes += dicomElementLength;
				} else {
					readBytes += bis.read(temp128, 0, dicomElementLength);
					value = Value.createValue(vr, temp128, dicomElementLength);
					// Composing DICOM Element
					dicomElement = 
						DicomElement.createNormalDicomElement(tag, value);
				}
				
			} else {
				// Reading the length of the element
				dicomElementLength = 
					DataMarshaller.getDicomUnsignedShort(temp4, 2);
				readBytes += bis.read(temp128, 0, dicomElementLength);
				value = Value.createValue(vr, temp128, dicomElementLength);
				// Composing DICOM Element
				dicomElement = 
					DicomElement.createNormalDicomElement(tag, value);
			}
			
			System.out.println(dicomElement);
			dicomElementList.add(dicomElement);
		}
		
		dicomObjectList.add(new DicomObject(dicomElementList));
		return dicomObjectList;
	}

}
