package com.d109.reper.response;

import com.d109.reper.domain.Ingredient;
import lombok.Getter;

@Getter
public class IngredientResponseDto {

    private String ingredientName;

    public IngredientResponseDto(Ingredient ingredient) {
        this.ingredientName = ingredient.getIngredientName();
    }
}
