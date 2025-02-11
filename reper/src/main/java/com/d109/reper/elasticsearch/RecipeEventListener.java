package com.d109.reper.elasticsearch;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeEventListener {

    private final RecipeSearchRepository recipeSearchRepository;

    @Autowired
    public RecipeEventListener (RecipeSearchRepository recipeSearchRepository) {
        this.recipeSearchRepository = recipeSearchRepository;
    }

    public void saveRecipeToElasticsearch(Recipe recipe) {

        RecipeDocument elasticRecipe = new RecipeDocument();
        elasticRecipe.setRecipeId(recipe.getRecipeId());
        elasticRecipe.setStoreId(recipe.getStore().getStoreId());
        elasticRecipe.setRecipeName(recipe.getRecipeName());
        elasticRecipe.setType(recipe.getType().name());
        elasticRecipe.setCategory(recipe.getCategory().name());
        elasticRecipe.setRecipeImg(recipe.getRecipeImg());

        // 재료 리스트 형태
        List<String> ingredients = recipe.getIngredients().stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toList());
        elasticRecipe.setIngredients(ingredients);

        // likedRecipe 기본 false로 설정
        elasticRecipe.setLikedRecipe(false);

        recipeSearchRepository.save(elasticRecipe);

        System.out.println("Elasticsearch에 저장 완료:" + elasticRecipe.getRecipeName());

    }
}
