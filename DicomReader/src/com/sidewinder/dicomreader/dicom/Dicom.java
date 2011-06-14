package com.sidewinder.dicomreader.dicom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
		byte[] temp2 = new byte[2];
		byte[] temp128 = new byte[128];
		
		ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		int vr;
		int dicomElementLength;
		
		Tag tag;
		Value<?> value = null;
		DicomElement<?> dicomElement = null;
		
		for (int i = 0; i < 10; i++) {
			// Reading DICOM Tag and Value Representation info
			bis.read(buffer.array(), buffer.position(), 8);
			buffer.get(temp4);
			tag = new Tag(temp4);

			// Reading Value Representation
			buffer.get(temp4);
			vr = Value.getVRIdentifier(new String(temp4, 0, 2));
			System.out.println(vr);
			
			// Reading and storing data
			if (Value.hasLongContent(vr)) {
				bis.read(temp4);
				// Reading the length of the element
				dicomElementLength = (int) DataMarshaller.getDicomUnsignedLong(temp4);
				bis.skip(dicomElementLength); //TODO: replace the skip with the actual reading of the file
			} else {
				// Reading the length of the element
				dicomElementLength = DataMarshaller.getDicomUnsignedShort(temp4, 2);
				bis.read(temp128, 0, dicomElementLength);
				value = Value.createValue(vr, temp128, dicomElementLength);
				System.out.println(value);
			}
			
			// Composing DICOM Element
			//dicomElement = new DicomElement(tag, value);
			
			buffer.clear();
		}
	}

}
