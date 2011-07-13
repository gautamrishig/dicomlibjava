package com.sidewinder.dicomreader.dicom.vr;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeValue extends Value {
	
	public static final int DA_LENGTH = 8;
	public static final int DT_LENGTH = 26;
	public static final int TM_LENGTH = 16;
	public static final int MINIMUM_TM_LENGTH = 2;
	
	protected DateTimeValue(int type, byte[] data, long contentLength) {
		super(type, data, contentLength);
	}

	@Override
	protected Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException {
		Date date = null;
		
		switch (getType()) {
		case Value.VR_DA:
			date = decodeDA(data, contentLength);
			break;
		case Value.VR_DT:
			date = decodeDT(data, contentLength);
			break;
		case Value.VR_TM:
			date = decodeTM(data, contentLength);
			break;
		}
		
		return date;
	}
	
	private Date decodeDA(byte[] data, long contentLength) 
			throws IllegalArgumentException {
		byte[] converted = new byte[DA_LENGTH];
		int ci = 0;
		int year;
		int month;
		int dayOfMonth;
		
		// Reformat the string if is in violation of current DICOM standards
		// but conforms to old nema (YYYY.MM.DD)
		for (int i = 0; i < contentLength; i++) {
			if (ci > DA_LENGTH) {
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
	
	// TODO: this method ignores milliseconds and time zone informations!
	private Date decodeDT(byte[] data, long contentLength) {
		int year = 0;
		int month = 0;
		int dayOfMonth = 0;
		int hourOfDay = 0;
		int minute = 0;
		int second = 0;
		
		if (contentLength > DT_LENGTH) {
			throw new IllegalArgumentException("DA Values must at most " +
					DT_LENGTH + " bytes long.");
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
	
	// TODO: this method ignores milliseconds!
	private Date decodeTM(byte[] data, long contentLength) {
		byte[] converted = new byte[TM_LENGTH];
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
			if (ci > TM_LENGTH) {
				throw new IllegalArgumentException("TM value does not conform " +
						"to neither ACR-NEMA nor DICOM standard.");
			}
			if (data[i] != ':') {
				converted[ci++] = data[i];
			}
		}
		contentLength = ci;
		data = converted;
		
		for (int i = 0; i < contentLength; i++) {
			System.out.println(data[i]);
		}
		
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

	// TODO: this method ignores milliseconds!
	@Override
	protected String getStringValue() {
		Calendar calendar = new GregorianCalendar();
		StringBuilder builder = new StringBuilder();
		
		calendar.setTime((Date) getValue());
		
		if (getType() == Value.VR_DA || getType() == Value.VR_DT) {
			builder.append(calendar.get(Calendar.DAY_OF_MONTH));
			builder.append(" ");
			builder.append(calendar.get(Calendar.MONTH));
			builder.append(" ");
			builder.append(calendar.get(Calendar.YEAR));
		}
		if (getType() == Value.VR_DT) {
			builder.append(" - ");
		}
		if (getType() == Value.VR_TM) {
			builder.append(calendar.get(Calendar.HOUR));
			builder.append(":");
			builder.append(calendar.get(Calendar.MINUTE));
			builder.append(":");
			builder.append(calendar.get(Calendar.SECOND));
			builder.append(" ");
			builder.append(calendar.get(Calendar.AM_PM));
		}
		
		return builder.toString();
	}
	
	@Override
	protected long getDicomLength(int type) throws IllegalArgumentException {
		switch (type) {
		case Value.VR_DA:
			return DA_LENGTH;
		case Value.VR_DT:
			return DT_LENGTH;
		case Value.VR_TM:
			return TM_LENGTH;
		default:
			throw new IllegalArgumentException("Value Representation " +
					type + " is not a valid DateTimeValue type.");
		}
	}

	protected static boolean isCompatible(int type) {
		switch (type) {
		case Value.VR_DA:
		case Value.VR_DT:
		case Value.VR_TM:
			return true;
		default:
			return false;
		}
	}
	
}
