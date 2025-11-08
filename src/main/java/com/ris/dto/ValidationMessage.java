package com.ris.dto;

public class ValidationMessage {

	private boolean isvalid;
	private String message;

	public ValidationMessage(boolean isvalid, String message) {
		super();
		this.isvalid = isvalid;
		this.message = message;
	}

	public boolean isIsvalid() {
		return isvalid;
	}

	public void setIsvalid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
