package com.sidewinder.dicomreader.dicom.values;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//TODO: this class ignores milliseconds!
public class DateTimeValue extends Value {
	
	protected DateTimeValue(int type, byte[] data, int contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		int year = 0;
		int month = 0;
		int dayOfMonth = 0;
		int hourOfDay = 0;
		int minute = 0;
		int second = 0;
		
		if (contentLength > getDicomLength()) {
			throw new IllegalArgumentException("DA Values must at most " +
					getDicomLength() + " bytes long.");
		}
		
		if (contentLength >= 4) {
			year = Integer.parseInt(new String(data, 0, 4));
		}
		if (contentLength >= 6) {
			month = Integer.parseInt(new String(data, 4, 6));
		}
		if (contentLength >= 8) {
			dayOfMonth = Integer.parseInt(new String(data, 6, 2));
		}
		if (contentLength >= 10) {
			hourOfDay = Integer.parseInt(new String(data, 8, 2));
		}
		if (contentLength >= 12) {
			minute = Integer.parseInt(new String(data, 10, 2));
		}
		if (contentLength >= 14) {
			second = Integer.parseInt(new String(data, 12, 2));
		}
		
		return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second).getTime();
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
		builder.append(" - ");
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
