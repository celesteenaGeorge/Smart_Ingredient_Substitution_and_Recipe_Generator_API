package com.ris.dto;

import lombok.Data;

@Data
public class SubstituteRequest {
	
	private String ingredient;
	private String dietaryAccommodation;
	private String allergens;
	private String recipeContext;

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public String getDietaryAccommodation() {
		return dietaryAccommodation;
	}

	public void setDietaryAccommodation(String dietaryAccommodation) {
		this.dietaryAccommodation = dietaryAccommodation;
	}

	public String getAllergens() {
		return allergens;
	}

	public void setAllergens(String allergens) {
		this.allergens = allergens;
	}

	public String getRecipeContext() {
		return recipeContext;
	}

	public void setRecipeContext(String recipeContext) {
		this.recipeContext = recipeContext;
	}

}
