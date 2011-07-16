package com.sidewinder.dicomreader.dicom.value;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeValue extends Value {
	
	private static final int MINIMUM_TM_LENGTH = 2;
	
	protected TimeValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException {
		byte[] converted = new byte[getDicomLength()];
		int ci = 0;
		int hourOfDay = 0;
		int minute = 0;
		int second = 0;
		
		if (contentLength < MINIMUM_TM_LENGTH) {
			throw new IllegalArgumentException("TM values must be at " +
					"least " + MINIMUM_TM_LENGTH + " bytes long.");
		}
		
		// Reformat the string if is in violation of current DICOM standards
		// but conforms to old nema (HH:MM:SS)
		for (int i = 0; i < contentLength; i++) {
			if (ci > getDicomLength()) {
				throw new IllegalArgumentException("TM value does not conform " +
						"to neither ACR-NEMA nor DICOM standard.");
			}
			if (data[i] != ':') {
				converted[ci++] = data[i];
			}
		}
		contentLength = ci;
		data = converted;
		
		if (contentLength >= 2) {
			hourOfDay = Integer.parseInt(new String(data, 0, 2));
		}
		if (contentLength >= 4) {
			minute = Integer.parseInt(new String(data, 2, 2));
		}
		if (contentLength >= 6) {
			second = Integer.parseInt(new String(data, 4, 2));
		}
		
		return new GregorianCalendar(0, 0, 0, hourOfDay, minute, second).getTime();
	}

	@Override
	protected String getStringValue() {
		Calendar calendar = new GregorianCalendar();
		StringBuilder builder = new StringBuilder();
		
		calendar.setTime((Date) getValue());
		
		builder.append(calendar.get(Calendar.HOUR));
		builder.append(":");
		builder.append(calendar.get(Calendar.MINUTE));
		builder.append(":");
		builder.append(calendar.get(Calendar.SECOND));
		builder.append(" ");
		builder.append(calendar.get(Calendar.AM_PM));
		
		return builder.toString();
	}

}
