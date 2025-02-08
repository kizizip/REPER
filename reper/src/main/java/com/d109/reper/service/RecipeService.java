package com.d109.reper.service;

import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.domain.Store;
import com.d109.reper.elasticsearch.NoticeDocument;
import com.d109.reper.elasticsearch.RecipeDocument;
import com.d109.reper.elasticsearch.RecipeSearchRepository;
import com.d109.reper.elasticsearch.StoreDocument;
import com.d109.reper.repository.RecipeRepository;
import com.d109.reper.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final StoreRepository storeRepository;
    private final RecipeSearchRepository recipeSearchRepository;
    private final EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    // 레시피 저장
    @Transactional
    public void saveRecipes(List<Recipe> recipes) {
//        logger.info("트랜잭션 시작 - 레시피 저장");

        for (Recipe recipe : recipes) {
//            logger.info("레시피 저장: {}", recipe.getRecipeName());
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
//            logger.info("레시피 저장 완료: {}", recipe.getRecipeName());
        }
    }

    // 레시피 조회(가게별)
    public List<Recipe> findRecipesByStore(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);

        if (store.isEmpty()) {
            throw new NoSuchElementException("Store not found.");
        }

        return recipeRepository.findByStore(storeId);
    }

    // 레시피 단건 조회
    public Recipe findRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findOne(recipeId);

        if (recipe == null) {
            throw new NoSuchElementException("Recipe not found.");
        }

        return recipe;
    }

    // 레시피 단건 삭제
    @Transactional
    public void deleteRecipe(Long recipeId) {

        Recipe recipe = recipeRepository.findOne(recipeId);

        if (recipe == null) {
            throw new NoSuchElementException("Recipe not found.");
        }

        recipeRepository.delete(recipeId);
    }


    // 가게별 레시피 검색
    public List<RecipeDocument> searchRecipeName(Long storeId, String keyword) {
        return recipeSearchRepository.findByStoreIdAndRecipeNameContaining(storeId, keyword);
    }


    // 가게별 레시피에서 재료 포함 검색
    public List<RecipeDocument> searchIncludeIngredient(Long storeId, String keyword) {
        return recipeSearchRepository.findByStoreIdAndIngredientsContaining(storeId, keyword);
    }


    // 재료 미포함 검색
    public List<RecipeDocument> searchExcludeIngredient(Long storeId, String keyword) {
        return recipeSearchRepository.findByStoreIdAndIngredientsIsNotContaining(storeId, keyword);
    }


    // ElasticSearch 레시피 동기화 test용 API
        // DB의 모든 레시피를 Elasticsearch로 동기화
    public void syncAllRecipesToElasticsearch() {
        List<Recipe> recipes = recipeRepository.findAllRecipes();  // DB에서 모든 레시피 가져오기
        List<RecipeDocument> recipeDocuments = recipes.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        recipeSearchRepository.saveAll(recipeDocuments);  // Elasticsearch에 저장
    }

        // Recipe 엔티티를 RecipeDocument 형태로 변환
    private RecipeDocument convertToDocument(Recipe recipe) {
        RecipeDocument document = new RecipeDocument();
        document.setRecipeId(recipe.getRecipeId());
        document.setStoreId(recipe.getStore().getStoreId());
        document.setRecipeName(recipe.getRecipeName());
        document.setType(recipe.getType().name());
        document.setCategory(recipe.getCategory().name());  // ENUM -> String 변환
        document.setRecipeImg(recipe.getRecipeImg());

        // Ingredient 이름만 추출하여 List<String>으로 변환
        List<String> ingredientNames = recipe.getIngredients().stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toList());
        document.setIngredients(ingredientNames);

        return document;
    }

}



