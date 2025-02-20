package com.d109.reper.response;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeJpaResponseDto {
    private Long recipeId;
    private Long storeId;
    private String recipeName;
    private String type;
    private String category;
    private String recipeImg;
    private Boolean likedRecipe;
    private List<String> ingredients;

    public static RecipeResponseDto fromEntity(Recipe recipe) {
        List<String> ingredientNames = recipe.getIngredients()
                .stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toList());

        return new RecipeResponseDto(
                recipe.getRecipeId(),
                recipe.getStore().getStoreId(),
                recipe.getRecipeName(),
                recipe.getType().name(),
                recipe.getCategory().name(),
                recipe.getRecipeImg(),
                false,  // likedRecipe는 항상 false
                ingredientNames
        );
    }
}
