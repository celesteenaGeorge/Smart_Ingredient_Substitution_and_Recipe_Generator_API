package com.ris.dto;

import java.util.List;

public class RecipeResponse {
    private String title;
    private int servings;
    private List<String> ingredients;
    private List<String> steps;
    private List<String> tips;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getTips() { return tips; }
    public void setTips(List<String> tips) { this.tips = tips; }
}