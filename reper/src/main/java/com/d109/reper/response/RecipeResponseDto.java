package com.d109.reper.response;

import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeCategory;
import com.d109.reper.domain.RecipeType;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeResponseDto {
    private Long recipeId;
    private String recipeName;
    private RecipeCategory category;
    private RecipeType type;
    private String recipeImg;
    private List<IngredientResponseDto> ingredients;
    private List<RecipeStepResponseDto> recipeSteps;

    public RecipeResponseDto(Recipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.recipeName = recipe.getRecipeName();
        this.category = recipe.getCategory();
        this.type = recipe.getType();
        this.recipeImg = recipe.getRecipeImg();
        this.ingredients = recipe.getIngredients().stream()
                .map(IngredientResponseDto::new) //new IngredientResponseDto(Ingredient)
                .toList();
        this.recipeSteps = recipe.getRecipeSteps().stream()
                .map(RecipeStepResponseDto::new)
                .toList();
    }


}
