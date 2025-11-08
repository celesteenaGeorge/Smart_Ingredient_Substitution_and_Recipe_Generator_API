package com.ris.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ris.dto.SubstituteRequest;
import com.ris.dto.ValidationResponse;
import com.ris.dto.ValidationResult;

@Service
public class ValidationService {

	@Autowired
	private OpenAIAPIService openAIAPIService;

	public List<ValidationResult> validateRecipeInput(SubstituteRequest substituteRequest) {
		try {
			Map<String, Object> body = generateAPIBody(substituteRequest);

			ValidationResponse validationResponse = openAIAPIService.callOpenAI(body, ValidationResponse.class);
			return validationResponse.getValidations();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch or parse OpenAI response: " + e.getMessage());
		}
	}

	// Generate OpenAI API body
	private Map<String, Object> generateAPIBody(SubstituteRequest substituteRequest) {
		Map<String, Object> systemMessage = Map.of("role", "system", "content", """
				You are a strict input validator for a recipe ingredient substitution API.
				Validate each field individually and return results as JSON matching the schema.
				Rules:
				- Ingredient: Must be a real, edible food item.
				- Allergens: Must be real allergens or food item or ingredient, this filed can also be null.
				- Dietary Accommodation: Do not validate this field.
				- Recipe Context: Must be a realistic recipe or cooking type (e.g., cake, stir-fry, curry).
				Respond ONLY with the JSON following the schema — no explanations or text.
				""");

		Map<String, Object> userMessage = Map.of("role", "user", "content", String.format("""
				Validate this recipe substitution request:
				Ingredient: %s
				Allergens: %s
				Dietary Accommodation: %s
				Recipe Context: %s
				""", substituteRequest.getIngredient(), substituteRequest.getAllergens(),
				substituteRequest.getDietaryAccommodation(), substituteRequest.getRecipeContext()));

		Map<String, Object> schema = createSchema();

		Map<String, Object> textFormat = Map.of("type", "json_schema", "name", "validation_schema", "schema", schema,
				"strict", true);

		return Map.of("model", "gpt-4o-2024-08-06", "input", List.of(systemMessage, userMessage), "text",
				Map.of("format", textFormat));
	}

	// JSON schema definition
	private Map<String, Object> createSchema() {
		return Map
				.of("type", "object", "properties",
						Map.of("validations",
								Map.of("type", "array", "items", Map.of("type", "object", "properties",
										Map.of("field", Map.of("type", "string"), "isvalid", Map.of("type", "boolean"),
												"explanation", Map.of("type", "string")),
										"required", List.of("field", "isvalid", "explanation"), "additionalProperties",
										false))),
						"required", List.of("validations"), "additionalProperties", false);
	}
}
