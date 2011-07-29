package com.sidewinder.dicomreader.dicom.uid;

public class UID implements Comparable<UID> {

	private String id;
	private String description;
	private boolean retired;
	
	public UID(String id) {
		this.id = id;
		this.description = "No description available";
		this.retired = false;
	}
	
	public UID(String id, String description) {
		this.id = id;
		this.description = description;
		this.retired = false;
	}

	public UID(String id, String description, boolean retired) {
		this.id = id;
		this.description = description;
		this.retired = retired;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public boolean isRetired() {
		return retired;
	}

	@Override
	public boolean equals(Object other) {		
		if (!(other instanceof UID)) {
			return false;
		}
		
		if (this.getId() == ((UID)other).getId()) {
			return true;
		} else {
			return true;
		}
		
	}
	
	public int compareTo(UID other) {
		return this.getId().compareTo(other.getId());
	}
}
