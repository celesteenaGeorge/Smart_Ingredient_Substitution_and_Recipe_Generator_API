package com.ris.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ris.dto.SubstituteRequest;
import com.ris.dto.SubstituteResponse;
import com.ris.dto.ValidationMessage;
import com.ris.dto.ValidationResult;

@Service
public class SubstituteService {

	@Value("${openai.api.key}")
	private String apiKey;
	@Value("${openai.api.url}")
	private String apiUrl;

	@Autowired
	private ValidationService validationService;
	@Autowired
	private OpenAIAPIService openAIAPIService;

	public SubstituteResponse getRecipeSubstitutions(SubstituteRequest substituteRequest) {
		// Validate request
		List<ValidationResult> validationResults = validationService.validateRecipeInput(substituteRequest);
		ValidationMessage validationMessage = isValid(validationResults);
		if (validationMessage.isIsvalid()) {
			Map<String, Object> body = generateAPIBody(substituteRequest);
			return openAIAPIService.callOpenAI(body, SubstituteResponse.class);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid input: " + validationMessage.getMessage());
		}
	}

	private ValidationMessage isValid(List<ValidationResult> validationResults) {
		for (ValidationResult result : validationResults) {
			if (!result.isIsvalid()) {
				// Return the first invalid field with explanation
				return new ValidationMessage(false, result.getExplanation());
			}
		}
		return new ValidationMessage(true, null);
	}

	private Map<String, Object> generateAPIBody(SubstituteRequest substituteRequest) {
		Map<String, Object> systemMessage = Map.of("role", "system", "content",
				"You are a helpful culinary assistant. Suggest  3 to 5 safe and appropriate ingredient substitutions considering allergies and dietary accommodations. Always respond strictly in JSON format.");
		Map<String, Object> userMessage = Map.of("role", "user", "content", String.format(
				"Suggest ingredient substitutions for '%s' in a '%s' recipe. The user follows '%s' diet and is allergic to '%s'.",
				substituteRequest.getIngredient(), substituteRequest.getRecipeContext(),
				substituteRequest.getDietaryAccommodation(), substituteRequest.getAllergens()));

		Map<String, Object> schema = CreateSchema();

		Map<String, Object> textFormat = Map.of("type", "json_schema", "name", "recipe_substitution_schema", "schema",
				schema, "strict", true);
		Map<String, Object> body = Map.of("model", "gpt-4o-2024-08-06", "input", List.of(systemMessage, userMessage),
				"text", Map.of("format", textFormat));
		return body;
	}

	// create JSON schema
	private Map<String, Object> CreateSchema() {

		Map<String, Object> schema = Map.of("type", "object", "properties",
				Map.of("substitutions", Map.of("type", "array", "items",
						Map.of("type", "object", "properties",
								Map.of("substitute", Map.of("type", "string"), "explanation", Map.of("type", "string")),
								"required", List.of("substitute", "explanation"), "additionalProperties", false))),
				"required", List.of("substitutions"), "additionalProperties", false);
		return schema;

	}
}
