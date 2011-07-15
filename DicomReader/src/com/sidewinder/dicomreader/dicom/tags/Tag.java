package com.sidewinder.dicomreader.dicom.tags;

import com.sidewinder.dicomreader.util.DataMarshaller;


public class Tag implements Comparable<Tag> {
	
	public static int ITEM_TAG = 0xFFFEE000 & 0xFFFFFFFF;
	public static int ITEM_DELIMITATION_TAG = 0xFFFEE00D & 0xFFFFFFFF;
	public static int SEQUENCE_DELIMITATION_TAG = 0xFFFEE0DD & 0xFFFFFFFF;
	public static int PIXEL_DATA_TAG = 0x7FE00010 & 0XFFFFFFFF;
	
	private static Tag itemTag = null;
	private static Tag itemDelimitationTag = null;
	private static Tag sequenceDelimitationTag = null;
	private static Tag pixelDataTag;
	
	private static final byte[] ITEM_TAG_BYTES =
		{(byte)0xFE, (byte)0xFF, (byte)0x00, (byte)0xE0};
	
	private static final byte[] ITEM_DELIMITATION_TAG_BYTES =
		{(byte)0xFE, (byte)0xFF, (byte)0x0D, (byte)0xE0};
	
	private static final byte[] SEQUENCE_DELIMITATION_TAG_BYTES =
		{(byte)0xFE, (byte)0xFF, (byte)0xDD, (byte)0xE0};
	
	private static final byte[] PIXEL_DATA_BYTES =
		{(byte)0xE0, (byte)0x7F, (byte)0x10, (byte)0x00};
	
	public static final int TAG_BYTE_LENGTH = 4;
	
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	private static int GROUP = 0;
	private static int ELEMENT = 2;
	
	private int group;
	private int element;
	
	private String groupString;
	private String elementString;
	
	public Tag(byte[] data) {
		// Store in int format
		group = DataMarshaller.getDicomUnsignedShort(data, GROUP);
		element = DataMarshaller.getDicomUnsignedShort(data, ELEMENT);
		
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
	
	public boolean isItemTag() {
		if (itemTag == null) {
			itemTag = new Tag(ITEM_TAG_BYTES);
		}
		
		return this.equals(itemTag);
	}
	
	public boolean isItemDelimitationTag() {
		if (itemDelimitationTag == null) {
			itemDelimitationTag = new Tag(ITEM_DELIMITATION_TAG_BYTES);
		}
		
		return this.equals(itemDelimitationTag);
	}
	
	public boolean isSequenceDelimitationTag() {
		if (sequenceDelimitationTag == null) {
			sequenceDelimitationTag = new Tag(SEQUENCE_DELIMITATION_TAG_BYTES);
		}
		
		return this.equals(sequenceDelimitationTag);
	}
	
	public boolean isPixelDataTag() {
		if (pixelDataTag == null) {
			pixelDataTag = new Tag(PIXEL_DATA_BYTES);
		}
		
		return this.equals(pixelDataTag);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("(");
		out.append(groupString).append(",").append(elementString).append(")");
		return out.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tag)) {
			return false;
		}
		
		Tag otherTag = (Tag) o;
		
		if (element != otherTag.getElement()) {
			return false;
		} else {
			return group == otherTag.getGroup();
		}
	}

	public int compareTo(Tag otherTag) {
		int groupDifference = group - otherTag.getGroup();
		
		if (groupDifference != 0) {
			return groupDifference;
		}
		
		return element - otherTag.getElement();
	}
}
