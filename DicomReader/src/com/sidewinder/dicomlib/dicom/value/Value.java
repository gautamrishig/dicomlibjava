package com.sidewinder.dicomlib.dicom.value;

import java.util.List;

import com.sidewinder.dicomlib.dicom.DicomObject;

public abstract class Value {

	// Value Representations identifiers
	public static final int UNIDENTIFIED = -1;
	public static final int AE = 0;
	public static final int AS = 1;
	public static final int AT = 2;
	public static final int CS = 3;
	public static final int DA = 4;
	public static final int DS = 5;
	public static final int DT = 6;
	public static final int FD = 7;
	public static final int FL = 8;
	public static final int IS = 9;
	public static final int LO = 10;
	public static final int LT = 11;
	public static final int OB = 12;
	public static final int OF = 13;
	public static final int OW = 14;
	public static final int PN = 15;
	public static final int SH = 16;
	public static final int SL = 17;
	public static final int SQ = 18;
	public static final int SS = 19;
	public static final int ST = 20;
	public static final int TM = 21;
	public static final int UI = 22;
	public static final int UL = 23;
	public static final int UN = 24;
	public static final int US = 25;
	public static final int UT = 26;

	// vsNames array column indexes
	private static final int SHORT_NAME = 0;
	private static final int LONG_NAME = 1;

	// Value Representation names
	private static final String[][] vrNames = { 
		{ "AE", "Application Entity" },
		{ "AS", "Age String" }, 
		{ "AT", "Attribute Tag" },
		{ "CS", "Code String" },
		{ "DA", "Date" },
		{ "DS", "Decimal String" },
		{ "DT", "Date Time" },
		{ "FD", "Floating Point Double" },
		{ "FL", "Floating Point Single" },
		{ "IS", "Integer String" },
		{ "LO", "Long String" },
		{ "LT", "Long Text" },
		{ "OB", "Other Byte String" },
		{ "OF", "Other Float String" },
		{ "OW", "Other Word String" },
		{ "PN", "Person Name" },
		{ "SH", "Short String" },
		{ "SL", "Signed Long" },
		{ "SQ", "Sequence of Items" },
		{ "SS", "Signed Short" },
		{ "ST", "Short Text" },
		{ "TM", "Time" },
		{ "UI", "Unique Identifier (UID)" },
		{ "UL", "Unsigned Long" },
		{ "UN", "Unknown" },
		{ "US", "Unsigned Short" },
		{ "UT", "Unlimited Text" }
	};
	
	private static final int[] vrLengths = {
		16,					// AE Application Entity
		4,					// AS Age String
		4,					// AT Attribute Tag
		16,					// CS Code String
		8,					// DA Date
		16,					// DS Decimal String
		26,					// DT Date Time
		8,					// FD Floating Point Double
		4,					// FL Floating Point Single
		12,					// IS Integer String
		64,					// LO Long String
		10240,				// LT Long Text
		-1,					// OB Other Byte
		Integer.MAX_VALUE,	// OF Other Float
		-1,					// OW Other Word
		320,				// PN Person Name
		16,					// SH Short String
		4,					// SL Signed Long
		-1,					// SQ Sequence of Items
		2,					// SS Signed Short
		1024,				// ST Short Text
		16,					// TM Time
		64,					// UI Unique Identifier (UID)
		4,					// UL Unsigned Long
		Integer.MAX_VALUE,	// UN Unknown
		2,					// US Unsigned Short
		Integer.MAX_VALUE	// UT Unlimited Text
	};
	
	private static final boolean[] vrFixedLength = {
		false,	// AE Application Entity
		true,	// AS Age String
		true,	// AT Attribute Tag
		false,	// CS Code String
		true,	// DA Date
		false,	// DS Decimal String
		false,	// DT Date Time
		true,	// FD Floating Point Double
		true,	// FL Floating Point Single
		false,	// IS Integer String
		false,	// LO Long String
		false,	// LT Long Text
		false,	// OB Other Byte
		false,	// OF Other Float
		false,	// OW Other Word
		false,	// PN Person Name
		false,	// SH Short String
		true,	// SL Signed Long
		false,	// SQ Sequence of Items
		true,	// SS Signed Short
		false,	// ST Short Text
		false,	// TM Time
		false,	// UI Unique Identifier (UID)
		true,	// UL Unsigned Long
		false,	// UN Unknown
		true,	// US Unsigned Short
		false	// UT Unlimited Text
	};

	private int type; // Type of VR
	private Object value;
	private long contentLength;

	/**
	 * Instantiate a new Value Representation.
	 * 
	 * @param type Value Representation type identifier.
	 * @param data Data to load in the current value.
	 * @param contentLength Length of the actual data in the byte array.
	 */
	protected Value(int type, byte[] data, int contentLength) {
		this.type = type;
		if (contentLength > data.length) {
			throw new IllegalArgumentException("Content length cannot be " +
					"longer than the data array.");
		}
		this.contentLength = contentLength;
		this.value = fromByteArray(data, contentLength);
	}
	
	/**
	 * Instantiate a new Value Representation.
	 * 
	 * @param type Value Representation type identifier.
	 * @param value Value to load in the current value.
	 * @param contentLength Length of the actual data in the byte array.
	 */
	protected Value(int type, Object value, int contentLength) {
		this.type = type;
		this.contentLength = contentLength;
		this.value = value;
	}

	/**
	 * Returns the integer identifier of the Value Representation.
	 * 
	 * @return Integer identifier of the Value Representation
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the short human-comprehensible name corresponding to the Value
	 * Representation identifier.
	 * 
	 * @return Value Representation short name
	 */
	public String getShortTypeName() {
		return getShortTypeName(type);
	}

	/**
	 * Returns the long human-comprehensible name corresponding to the Value
	 * Representation identifier.
	 * 
	 * @return Value Representation long name
	 */
	public String getLongTypeName() {
		return getLongTypeName(type);
	}

	/**
	 * Returns the value stored inside the class. 
	 * @return value stored inside the class.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the length (in bytes) of the value currently stored inside the
	 * class. This may differ from the length specified in the DICOM standard
	 * since, as a rule, this DICOM Library will try to read malformed DICOM
	 * files.
	 * 
	 * @return Length (in bytes) of the class content.
	 */
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public String toString() {
		return "(" + getShortTypeName() + ") " + getStringValue();
	}

	/**
	 * Returns the short human-comprehensible name corresponding to the Value
	 * Representation identifier.
	 * 
	 * @param type Value Representation Identifier.
	 * @return Value Representation short name.
	 */
	public static String getShortTypeName(int type) {
		if (type < vrNames.length) {
			return vrNames[type][SHORT_NAME];
		} else {
			return null;
		}
	}

	/**
	 * Returns the long human-comprehensible name corresponding to the Value
	 * Representation identifier.
	 * 
	 * @param type Value Representation Identifier.
	 * @return Value Representation long name.
	 */
	public static String getLongTypeName(int type) {
		if (type < vrNames.length) {
			return vrNames[type][LONG_NAME];
		} else {
			return null;
		}
	}
	
	public static Value createValue(int type, byte[] data,
			long contentLength) throws IllegalArgumentException {
		
		//TODO: we assume 2^32/2 is the maximum length for any of the elements
		switch (type) {
		case AE:
			return new ApplicationEntityValue(type, data, (int)contentLength);
		case AS:
			return new AgeStringValue(type, data, (int)contentLength);
		case AT:
			return new AttributeTagValue(type, data, (int)contentLength);
		case CS:
			return new CodeStringValue(type, data, (int)contentLength);
		case DA:
			return new DateValue(type, data, (int)contentLength);
		case DS:
			return new DecimalStringValue(type, data, (int)contentLength);
		case DT:
			return new DateTimeValue(type, data, (int)contentLength);
		case FD:
			return new FloatingDoubleValue(type, data, (int)contentLength);
		case FL:
			return new FloatingSingleValue(type, data, (int)contentLength);
		case IS:
			return new IntegerStringValue(type, data, (int)contentLength);
		case LO:
			return new LongStringValue(type, data, (int)contentLength);
		case LT:
			return new LongTextValue(type, data, (int)contentLength);
		case OB:
			return new OtherByteValue(type, data, (int)contentLength);
		case OF:
			return new OtherFloatValue(type, data, (int)contentLength);
		case OW:
			return new OtherWordValue(type, data, (int)contentLength);
		case PN:
			return new PersonNameValue(type, data, (int)contentLength);
		case SH:
			return new ShortStringValue(type, data, (int)contentLength);
		case SL:
			return new SignedLongValue(type, data, (int)contentLength);
		case ST:
			return new ShortTextValue(type, data, (int)contentLength);
		case SS:
			return new SignedShortValue(type, data, (int)contentLength);
		case TM:
			return new TimeValue(type, data, (int)contentLength);
		case UI:
			return new UniqueIdentifierValue(type, data, (int)contentLength);
		case UL:
			return new UnsignedLongValue(type, data, (int)contentLength);
		case UN:
			return new UnknownValue(type, data, (int)contentLength);
		case US:
			return new UnsignedShortValue(type, data, (int)contentLength);
		case UT:
			return new UnlimitedTextValue(type, data, (int)contentLength);
			
		default:
			throw new IllegalArgumentException(type + " is not a valid" +
					" Value Representation Identifier.");
		}
	}
	
	public static SequenceValue createContainerValue(
			List<DicomObject> elements, long contentLength)
			throws IllegalArgumentException {
		
		if (elements == null) {
			throw new IllegalArgumentException("SQ Value Representations " +
					"cannot contain a null list.");
		}
		
		//TODO: we assume 2^32/2 is the maximum length for any of the elements
		return new SequenceValue(elements, (int)contentLength);
	}

	/**
	 * Returns the integer identifier corresponding to the Value Representation
	 * passed as a parameter.
	 * 
	 * @param vr Short Type Name of the Value Representation.
	 * @return Integer identifier of the Value Representation.
	 */
	public static int getVRIdentifier(String vr) {
		vr = vr.toUpperCase();

		for (int i = 0; i < vrNames.length; i++) {
			if (vrNames[i][0].equals(vr)) {
				return i;
			}
		}

		return UNIDENTIFIED;
	}
	
	/**
	 * Checks if the identifier passed as a parameter corresponds to a
	 * Value Representation uses a 4 byte length in Explicit mode.
	 * 
	 * @param type Value Representation type to check
	 * @return True if the content of the current Value Representation uses
	 * 4 bytes to represent its length when in Explicit mode
	 */
	public static boolean has4BytesLength(int type) {
		switch (type) {
		case Value.OF:
		case Value.OB:
		case Value.OW:
		case Value.SQ:
		case Value.UN:
		case Value.UT:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Returns the length (in bytes) of the current Value Representation as
	 * specified by the DICOM standard. Depending on the instance, this value
	 * can be a maximum length or a mandatory length.
	 * 
	 * @return Value Representation length (in bytes) as described in the DICOM
	 * standard. -1 if the standard does not prescribe any length.
	 */
	public int getDicomLength() {
		return getDicomLength(type);
	}
	
	public boolean isFixedLength() {
		return isFixedLength(type);
	}
	
	public static int getDicomLength(int type) {
		return vrLengths[type];
	}
	
	public static boolean isFixedLength(int type) {
		return vrFixedLength[type];
	}

	/**
	 * Decodes the byte array read from the file into the appropriate Value
	 * Representation. No length checks are done during this phase, the
	 * information read from the DICOM file is stored even if is longer than
	 * what is prescribed by the standard.
	 * This method is implemented in the classes that inherit from Value.
	 * 
	 * @param data DICOM Element data to convert and store in the object.
	 * @param contentLength Length of the actual data content to be converted.
	 * @return Java representation of the DICOM Element data.
	 * @throws IllegalArgumentException If the values passed as a parameter
	 * are not suitable to perform the decode action.
	 */
	protected abstract Object fromByteArray(byte[] data, int contentLength)
			throws IllegalArgumentException;

	/**
	 * Accessory method needed to represent the value of the class in a
	 * String format.
	 * This method is implemented in the classes that inherit from Value.
	 * 
	 * @return String representation of the value of the class. 
	 */
	protected abstract String getStringValue();
	
}
