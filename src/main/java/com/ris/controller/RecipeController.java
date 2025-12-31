package com.ris.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ris.dto.RecipeResponse;
import com.ris.dto.SubstituteRequest;
import com.ris.dto.SubstituteResponse;
import com.ris.dto.ValidationResult;
import com.ris.service.RecipeService;
import com.ris.service.SubstituteService;
import com.ris.service.ValidationService;

@RestController
@RequestMapping("/api")
public class RecipeController {

	@Autowired
	private SubstituteService substituteService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private ValidationService ValidationService;

	@PostMapping("/substitute")
	public SubstituteResponse getRecipeSubstitutions(@RequestBody SubstituteRequest request) {

		return substituteService.getRecipeSubstitutions(request);
	}

	@PostMapping("/recipe")
	public RecipeResponse getRecipe(@RequestBody SubstituteRequest request) {

		return recipeService.generateRecipe(request);
	}

	@PostMapping("/valid")
	public List<ValidationResult> valid(@RequestBody SubstituteRequest request) {

		return ValidationService.validateRecipeInput(request);
	}

}