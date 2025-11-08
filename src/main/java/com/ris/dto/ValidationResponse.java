package com.ris.dto;

import java.util.List;

public class ValidationResponse {
	private List<ValidationResult> validations;

	public List<ValidationResult> getValidations() {
		return validations;
	}

	public void setValidations(List<ValidationResult> validations) {
		this.validations = validations;
	}
}