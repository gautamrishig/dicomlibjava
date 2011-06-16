package com.sidewinder.dicomreader.dicom.vr;

public abstract class Value {

	// Value Representations identifiers
	public static final int UNIDENTIFIED = -1;
	public static final int VR_AE = 0;
	public static final int VR_AS = 1;
	public static final int VR_AT = 2;
	public static final int VR_CS = 3;
	public static final int VR_DA = 4;
	public static final int VR_DS = 5;
	public static final int VR_DT = 6;
	public static final int VR_FL = 7;
	public static final int VR_FD = 8;
	public static final int VR_IS = 9;
	public static final int VR_LO = 10;
	public static final int VR_LT = 11;
	public static final int VR_OB = 12;
	public static final int VR_OF = 13;
	public static final int VR_OW = 14;
	public static final int VR_PN = 15;
	public static final int VR_SH = 16;
	public static final int VR_SL = 17;
	public static final int VR_SQ = 18;
	public static final int VR_SS = 19;
	public static final int VR_ST = 20;
	public static final int VR_TM = 21;
	public static final int VR_UI = 22;
	public static final int VR_UL = 23;
	public static final int VR_UN = 24;
	public static final int VR_US = 25;
	public static final int VR_UT = 26;

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
		{ "FL", "Floating Point Single" },
		{ "FD", "Floating Point Double" },
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
		{ "UT", "Unlimited Text" } };

	private int type; // Type of VR (see VRFactory)
	private Object value;
	private long length;
	private long contentLength;

	/**
	 * Instantiate a new Value Representation.
	 * 
	 * @param type Value Representation type identifier.
	 * @param length Length of the data.
	 * @param data Data to load in the current value.
	 * @param contentLength Length of the actual data in the byte array.
	 */
	protected Value(int type, byte[] data, long contentLength) {
		this.type = type;
		this.length = getDicomLength(type);
		if (contentLength > data.length) {
			throw new IllegalArgumentException("Content length cannot be " +
					"longer than the data array.");
		}
		this.contentLength = contentLength;
		this.value = fromByteArray(data, contentLength);
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
	 * Returns the length (in bytes) of the current Value Representation as
	 * specified by the DICOM standard. Depending on the instance, this value
	 * can be a maximum length or a mandatory length.
	 * 
	 * @return Value Representation length (in bytes) as described in the DICOM
	 * standard. -1 if the standard does not prescribe any length.
	 */
	public long getLength() {
		return length;
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
		return getStringValue() + " (" + getShortTypeName() + ")";
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
		
		if (StringValue.isCompatible(type)) {
			return new StringValue(type, data, contentLength);
		} else if (UnsignedValue.isCompatible(type)){
			return new UnsignedValue(type, data, contentLength);
		} else if (UniqueIdentifierValue.isCompatible(type)) {
			return new UniqueIdentifierValue(type, data, contentLength);
		} else if (ApplicationEntityValue.isCompatible(type)) {
			return new ApplicationEntityValue(type, data, contentLength);
		} else if (DateTimeValue.isCompatible(type)){
			return new DateTimeValue(type, data, contentLength);
		} else if (OtherByteValue.isCompatible(type)) {
			return new OtherByteValue(type, data, contentLength);
		} else if (TextValue.isCompatible(type)) {
			return new TextValue(type, data, contentLength);
		} else if (PersonNameValue.isCompatible(type)){
			return new PersonNameValue(type, data, contentLength);
		} else {
			throw new IllegalArgumentException(type + " is not a valid" +
					" Value Representation Identifier.");
		}
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
	 * Value Representation that may contain more than 128 bytes of data.
	 * 
	 * @param type Value Representation type to check
	 * @return True if the content of the current Value Representation may
	 * be over 128 bytes
	 */
	public static boolean hasLongContent(int type) {
		switch (type) {
		case Value.VR_LT:
		case Value.VR_OF:
		case Value.VR_OB:
		case Value.VR_OW:
		case Value.VR_ST:
		case Value.VR_SQ:
		case Value.VR_UN:
		case Value.VR_UT:
			return true;
		default:
			return false;
		}
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
		case Value.VR_OF:
		case Value.VR_OB:
		case Value.VR_OW:
		case Value.VR_SQ:
		case Value.VR_UN:
		case Value.VR_UT:
			return true;
		default:
			return false;
		}
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
	protected abstract Object fromByteArray(byte[] data, long contentLength)
			throws IllegalArgumentException;

	/**
	 * Accessory method needed to represent the value of the class in a
	 * String format.
	 * This method is implemented in the classes that inherit from Value.
	 * 
	 * @return String representation of the value of the class. 
	 */
	protected abstract String getStringValue();
	
	protected abstract long getDicomLength(int type);
	
}
