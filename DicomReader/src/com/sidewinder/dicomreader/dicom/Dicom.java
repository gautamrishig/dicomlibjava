package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.sidewinder.dicomreader.dicom.dicomelement.DicomElement;
import com.sidewinder.dicomreader.dicom.dicomelement.NormalDicomElement;
import com.sidewinder.dicomreader.dicom.tags.Tag;
import com.sidewinder.dicomreader.dicom.vr.Value;
import com.sidewinder.dicomreader.util.DataMarshaller;

public class Dicom {

	private static final int READ_BUFFER_SIZE = 128;

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

			parseExplicit(bis);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseExplicit(BufferedInputStream bis)
			throws IOException {
		byte[] temp4 = new byte[4];
		byte[] temp128 = new byte[128];
		
		ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		int vr;
		int dicomElementLength;
		
		Tag tag;
		Value value = null;
		DicomElement dicomElement = null;
		
		while (bis.available() > 0) {
			// Reading DICOM Tag and Value Representation info
			bis.read(buffer.array(), buffer.position(), 8);
			buffer.get(temp4);
			tag = new Tag(temp4);

			// Reading Value Representation
			buffer.get(temp4);
			vr = Value.getVRIdentifier(new String(temp4, 0, 2));
			
			// Reading and storing data
			if (Value.hasLongContent(vr)) {
				
				if (Value.has4BytesLength(vr)) {
					// Reading the length of the element (4 bytes length)
					bis.read(temp4);
					dicomElementLength = 
						(int) DataMarshaller.getDicomUnsignedLong(temp4);
				} else {
					// Reading the length of the element (2 bytes length)
					dicomElementLength =
						DataMarshaller.getDicomUnsignedShort(temp4, 2);
				}
				
				if (dicomElementLength > 128) {
					bis.read(temp128);
					value = Value.createValue(vr, temp128, 128);
					bis.skip(dicomElementLength - 128);
					// Composing DICOM Element
					System.out.println("preview");
					dicomElement = DicomElement.createPreviewDicomElement(tag,
							value, 0, dicomElementLength); //TODO: change with the correct value instead of 0 for the file position!!
				} else {
					bis.read(temp128, 0, dicomElementLength);
					value = Value.createValue(vr, temp128, dicomElementLength);
					// Composing DICOM Element
					dicomElement = DicomElement.createNormalDicomElement(tag, value);
				}
				
			} else {
				// Reading the length of the element
				dicomElementLength = DataMarshaller.getDicomUnsignedShort(temp4, 2);
				bis.read(temp128, 0, dicomElementLength);
				value = Value.createValue(vr, temp128, dicomElementLength);
				// Composing DICOM Element
				dicomElement = DicomElement.createNormalDicomElement(tag, value);
			}
			
			System.out.println(dicomElement);
			
			buffer.clear();
		}
	}

}
