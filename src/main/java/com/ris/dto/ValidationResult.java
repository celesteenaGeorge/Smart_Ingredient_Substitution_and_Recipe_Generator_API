package com.ris.dto;

public class ValidationResult {
    private String field;
    private boolean isvalid;
    private String explanation;  

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isIsvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean isvalid) {
        this.isvalid = isvalid;
    }
}