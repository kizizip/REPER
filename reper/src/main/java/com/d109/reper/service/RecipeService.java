package com.d109.reper.service;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final EntityManager em;

    //레시피 등록
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Transactional
    public void saveRecipes(List<Recipe> recipes) {
        logger.info("트랜잭션 시작 - 레시피 저장");

        for (Recipe recipe : recipes) {
            logger.info("레시피 저장: {}", recipe.getRecipeName());

            // 생성 시간 설정
            recipe.setCreatedAt(LocalDateTime.now());

            // 레시피 단계 저장
            List<RecipeStep> steps = recipe.getRecipeSteps();

            for (int i = 0; i < recipe.getRecipeSteps().size(); i++) {
                RecipeStep oldStep = steps.get(i);

                // 새로운 RecipeStep 객체 생성
                RecipeStep newStep = new RecipeStep();
                newStep.setRecipe(recipe);  // 레시피와 연관 설정
                newStep.setStepNumber(i + 1);
                newStep.setInstruction(oldStep.getInstruction());  // 💡 instruction 값 저장
                newStep.setCreatedAt(LocalDateTime.now());
                newStep.setUpdatedAt(LocalDateTime.now());

                recipe.addRecipeStep(newStep);
                logger.info("✔ 저장할 레시피 단계 {}: {}", i + 1, newStep.getInstruction());
            }

            // 재료 저장
            List<Ingredient> ingredients = recipe.getIngredients();
            recipe.setIngredients(new ArrayList<>()); // 기존 재료 리스트 초기화

            for (Ingredient ingredient : ingredients) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setIngredientName(ingredient.getIngredientName());
                recipe.addIngredient(newIngredient);
            }

            // 레시피 저장
            recipeRepository.save(recipe);
            logger.info("레시피 저장 완료: {}", recipe.getRecipeName());
        }
    }



    //레시피 조회(가게별)
    public List<Recipe> findRecipesByStore(Long storeId) {
        return recipeRepository.findByStore(storeId);
    }

    //레시피 조회(단건)
    public Recipe findRecipe(Long recipeId) {
        return recipeRepository.findOne(recipeId);
    }

    //레시피 삭제(단건)
    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.delete(recipeId);
    }



}
