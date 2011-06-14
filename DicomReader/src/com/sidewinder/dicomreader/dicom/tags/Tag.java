package com.sidewinder.dicomreader.dicom.tags;

import com.sidewinder.dicomreader.util.DataMarshaller;


public class Tag {
	
	public static final int TAG_BYTE_LENGTH = 4;
	
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	private static int GROUP = 0;
	private static int ELEMENT = 1;
	
	private int group;
	private int element;
	
	private String groupString;
	private String elementString;
	
	public Tag(byte[] data) {
		// Store in int format
		group = DataMarshaller.getDicomUnsignedShort(data, 0);
		group = DataMarshaller.getDicomUnsignedShort(data, 2);
		
		// Store in String format
		groupString = tagToHex(data, GROUP);
		elementString = tagToHex(data, ELEMENT);
	}
	
	public int getGroup() {
		return group;
	}
	
	public int getElement() {
		return element;
	}
	
	private static String tagToHex(byte[] data, int position) {
		char[] temp = new char[4];
		
		temp[0] = HEX_CHARS[(data[position + 1] & 0xF0) >> 4];
		temp[1] = HEX_CHARS[(data[position + 1] & 0x0F)];
		temp[2] = HEX_CHARS[(data[position] & 0xF0) >> 4];
		temp[3] = HEX_CHARS[(data[position] & 0x0F)];
		
		return new String(temp);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("(");
		out.append(groupString).append(",").append(elementString).append(")");
		return out.toString();
	}

}
