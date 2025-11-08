package com.ris.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SubstituteResponse {

	private List<SubstituteOption> substitutions;
	@JsonIgnore
	private ValidationMessage validationMessage;

	public List<SubstituteOption> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(List<SubstituteOption> substitutions) {
		this.substitutions = substitutions;
	}

	public ValidationMessage getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(ValidationMessage validationMessage) {
		this.validationMessage = validationMessage;
	}

}