package com.d109.reper.response;

import com.d109.reper.domain.Recipe;
import lombok.Getter;


@Getter
public class FavoriteResponseDto {
    private Long recipeId;
    private String recipeName;
    private String recipeImg;

    public FavoriteResponseDto(Recipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.recipeName = recipe.getRecipeName();
        this.recipeImg = recipe.getRecipeImg();
    }



}
