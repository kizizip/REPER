package com.d109.reper.service;

import com.d109.reper.domain.Animation;
import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.repository.AnimationRepository;
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
    private final AnimationRepository animationRepository;
    private final EntityManager em;

    //레시피 등록
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Transactional
    public void saveRecipes(List<Recipe> recipes) {
        logger.info("트랜잭션 시작 - 레시피 저장");

        // 1) animation 테이블에 저장된 모든 Animation 정보 조회
        List<Animation> animations = animationRepository.findAll();

        // 2) 레시피 등록 로직
        for (Recipe recipe : recipes) {
            logger.info("레시피 저장: {}", recipe.getRecipeName());

            // 생성 시간 설정
            recipe.setCreatedAt(LocalDateTime.now());

            // 레시피 단계 저장
            List<RecipeStep> steps = recipe.getRecipeSteps();

            // 기존 steps 리스트 복사
            List<RecipeStep> originalSteps = new ArrayList<>(recipe.getRecipeSteps());
            recipe.getRecipeSteps().clear();  // 기존 리스트 비우기

            // 각 step에 대해 필요한 정보만 설정
            for (int i = 0; i < originalSteps.size(); i++) {
                RecipeStep originalStep = originalSteps.get(i);

                RecipeStep step = new RecipeStep();
                step.setInstruction(originalStep.getInstruction());
                step.setStepNumber(i + 1);
                step.setCreatedAt(LocalDateTime.now());
                step.setUpdatedAt(LocalDateTime.now());

                // 3) step.instruction에 애니메이션 keyword가 포함되어 있는지 확인
                for (Animation animation : animations) {
                    if (step.getInstruction().contains(animation.getKeyword())) {
                        step.setAnimationUrl(animation.getAnimationUrl());
                        break;
                    }
                }

                recipe.addRecipeStep(step);  // 양방향 연관관계 메서드
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
