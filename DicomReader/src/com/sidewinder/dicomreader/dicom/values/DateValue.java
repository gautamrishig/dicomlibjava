package com.sidewinder.dicomreader.dicom.values;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateValue extends Value {
	
	protected DateValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		byte[] converted = new byte[getDicomLength()];
		int ci = 0;
		int year;
		int month;
		int dayOfMonth;
		
		// Reformat the string if is in violation of current DICOM standards
		// but conforms to old nema (YYYY.MM.DD)
		for (int i = 0; i < contentLength; i++) {
			if (ci > getDicomLength()) {
				throw new IllegalArgumentException("DA Value does not conform " +
						"to neither ACR-NEMA nor DICOM date standards");
			}
			if (data[i] != '.') {
				converted[ci++] = data[i];
			}
		}
		contentLength = ci;
		data = converted;
		
		year = Integer.parseInt(new String(data, 0, 4));
		month = Integer.parseInt(new String(data, 4, 2));
		dayOfMonth = Integer.parseInt(new String(data, 6, 2));
		
		return new GregorianCalendar(year, month, dayOfMonth).getTime();
	}

	@Override
	protected String getStringValue() {
		Calendar calendar = new GregorianCalendar();
		StringBuilder builder = new StringBuilder();
		
		calendar.setTime((Date) getValue());
		
		builder.append(calendar.get(Calendar.DAY_OF_MONTH));
		builder.append(" ");
		builder.append(calendar.get(Calendar.MONTH));
		builder.append(" ");
		builder.append(calendar.get(Calendar.YEAR));
		
		return builder.toString();
	}

}
