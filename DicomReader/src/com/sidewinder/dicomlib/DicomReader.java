package com.sidewinder.dicomlib;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.sidewinder.dicomlib.dicom.DicomFileContext;
import com.sidewinder.dicomlib.dicom.DicomFileReader;
import com.sidewinder.dicomlib.exception.MalformedDicomException;

public class DicomReader {

	public static void main(String[] args) throws MalformedDicomException {
		String outputFileName;
		DicomFileContext context;
		
		//context = DicomFileReader.readDicomFile("/Users/sidewinder/Desktop/dicom/IM-0001-0008.dcm");
		//context = DicomFileReader.readDicomFile("/Users/sidewinder/Desktop/dicom/US-RGB-8-esopecho.dcm");
		//context = DicomFileReader.readDicomFile("/Users/sidewinder/Desktop/dicom/B7QJ54U4.dcm");
		//context = DicomFileReader.readDicomFile("/Users/sidewinder/Desktop/dicom/testGE/B7SG6E8C.dcm");
		context = DicomFileReader.readDicomFile("/Users/sidewinder/Desktop/dicom/testGE/B7SG6E8E.dcm");
		
		if (context != null) {
			outputFileName = context.getAbsolutePath() + ".txt";
			System.out.println("Exporting parse output to file " + outputFileName);
			
			try {
				FileOutputStream fos = new FileOutputStream(outputFileName);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				osw.write(context.getDicomObject().toString());
				osw.close();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
