package com.d109.reper.elasticsearch;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeEventListener {

    private final RecipeSearchRepository recipeSearchRepository;
    private final RecipeDocument recipeDocument;

    @Autowired
    public RecipeEventListener (RecipeSearchRepository recipeSearchRepository, RecipeDocument recipeDocument) {
        this.recipeSearchRepository = recipeSearchRepository;
        this.recipeDocument = recipeDocument;
    }

    public void saveRecipeToElasticsearch(Recipe recipe) {

        RecipeDocument elasticRecipe = new RecipeDocument();
        recipeDocument.setRecipeId(recipe.getRecipeId());
        recipeDocument.setStoreId(recipe.getStore().getStoreId());
        recipeDocument.setRecipeName(recipe.getRecipeName());
        recipeDocument.setType(recipe.getType().name());
        recipeDocument.setCategory(recipe.getCategory().name());
        recipeDocument.setRecipeImg(recipe.getRecipeImg());

        // 재료 리스트 형태
        List<String> ingredients = recipe.getIngredients().stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toList());
        recipeDocument.setIngredients(ingredients);

        recipeSearchRepository.save(elasticRecipe);

        System.out.println("Elasticsearch에 저장 완료:" + elasticRecipe.getRecipeName());

    }
}
