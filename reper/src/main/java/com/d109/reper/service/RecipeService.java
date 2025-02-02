package com.d109.reper.service;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    // 레시피 저장
    @Transactional
    public void saveRecipes(List<Recipe> recipes) {
        logger.info("트랜잭션 시작 - 레시피 저장");

        for (Recipe recipe : recipes) {
            logger.info("레시피 저장: {}", recipe.getRecipeName());
            recipe.setCreatedAt(LocalDateTime.now());

            // 레시피 단계(RecipeStep) 처리
            List<RecipeStep> originalSteps = new ArrayList<>(recipe.getRecipeSteps());
            recipe.getRecipeSteps().clear();  // 기존 단계 리스트 초기화

            for (int i = 0; i < originalSteps.size(); i++) {
                RecipeStep originalStep = originalSteps.get(i);
                RecipeStep step = new RecipeStep();
                
                step.setInstruction(originalStep.getInstruction());
                step.setAnimationUrl(originalStep.getAnimationUrl());
                step.setStepNumber(i + 1);
                step.setCreatedAt(LocalDateTime.now());
                step.setUpdatedAt(LocalDateTime.now());

                recipe.addRecipeStep(step);  // 양방향 연관관계 설정
            }

            // 재료 처리
            List<Ingredient> ingredients = recipe.getIngredients();
            recipe.setIngredients(new ArrayList<>());
            for (Ingredient ingredient : ingredients) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setIngredientName(ingredient.getIngredientName());
                recipe.addIngredient(newIngredient);
            }

            recipeRepository.save(recipe);
            logger.info("레시피 저장 완료: {}", recipe.getRecipeName());
        }
    }

    // 레시피 조회(가게별)
    public List<Recipe> findRecipesByStore(Long storeId) {
        return recipeRepository.findByStore(storeId);
    }

    // 레시피 단건 조회
    public Recipe findRecipe(Long recipeId) {
        return recipeRepository.findOne(recipeId);
    }

    // 레시피 단건 삭제
    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.delete(recipeId);
    }
}
