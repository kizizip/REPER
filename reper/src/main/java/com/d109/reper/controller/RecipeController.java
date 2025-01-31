package com.d109.reper.controller;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    //레시피 등록
    @PostMapping("/stores/{storeId}/recipes")
    public ResponseEntity<Void> createRecipes(@PathVariable Long storeId, @RequestBody List<Recipe> recipes) {
        recipeService.saveRecipes(recipes);
        return ResponseEntity.ok().build();
    }


    //레시피 조회(가게별)
    @GetMapping("/stores/{storeId}/recipes")
    public ResponseEntity<List<Recipe>> getRecipeByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(recipeService.findRecipesByStore(storeId));
    }

    //레시피 조회(단건)
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.findRecipe(recipeId));
    }

    //레시피 삭제(단건)
    @DeleteMapping("/recipes/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok().build();
    }
}
