package com.ris.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.ris.dto.RecipeResponse;
import com.ris.dto.SubstituteRequest;
import com.ris.dto.ValidationMessage;
import com.ris.dto.ValidationResult;

@Service
public class RecipeService {

	@Autowired
	private ValidationService validationService;
	@Autowired
	private OpenAIAPIService openAIAPIService;

	public RecipeResponse generateRecipe(SubstituteRequest request) {

		// Validate request
		List<ValidationResult> validationResults = validationService.validateRecipeInput(request);
		ValidationMessage validationMessage = isValid(validationResults);

		if (validationMessage.isIsvalid()) {

			// Build OpenAI request body
			Map<String, Object> body = generateAPIBody(request);
			return openAIAPIService.callOpenAI(body, RecipeResponse.class);
		}

		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid input: " + validationMessage.getMessage());
		}

	}

	private ValidationMessage isValid(List<ValidationResult> validationResults) {
		for (ValidationResult result : validationResults) {
			if (!result.isIsvalid()) {
				return new ValidationMessage(false, result.getExplanation());
			}
		}
		return new ValidationMessage(true, null);
	}

	private Map<String, Object> generateAPIBody(SubstituteRequest request) {

		Map<String, Object> systemMessage = Map.of("role", "system", "content", """
				You are a professional culinary assistant.
				Generate a creative but easy-to-follow recipe for 2 servings
				based on the user's ingredient, dietary accommodations, and allergens.
				Include clear steps and practical cooking tips.
				Respond strictly in JSON format following the schema provided.
				""");

		Map<String, Object> userMessage = Map.of("role", "user", "content", String.format("""
				Create a recipe for a '%s' that serves 2 people.
				The user follows a '%s' diet and is allergic to '%s'.
				The main ingredient is '%s'.
				Include ingredient list, steps, and 2 helpful cooking tips.
				""", request.getRecipeContext(), request.getDietaryAccommodation(), request.getAllergens(),
				request.getIngredient()));

		Map<String, Object> schema = createSchema();

		Map<String, Object> textFormat = Map.of("type", "json_schema", "name", "recipe_generation_schema", "schema",
				schema, "strict", true);

		return Map.of("model", "gpt-4o-2024-08-06", "input", List.of(systemMessage, userMessage), "text",
				Map.of("format", textFormat));
	}

	private Map<String, Object> createSchema() {
		return Map.of("type", "object", "properties",
				Map.of("title", Map.of("type", "string"), "servings", Map.of("type", "integer"), "ingredients",
						Map.of("type", "array", "items", Map.of("type", "string")), "steps",
						Map.of("type", "array", "items", Map.of("type", "string")), "tips",
						Map.of("type", "array", "items", Map.of("type", "string"))),
				"required", List.of("title", "servings", "ingredients", "steps", "tips"), "additionalProperties",
				false);
	}
}
