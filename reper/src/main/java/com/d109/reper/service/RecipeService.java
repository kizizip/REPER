package com.d109.reper.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.domain.Store;
import com.d109.reper.elasticsearch.RecipeDocument;
import com.d109.reper.elasticsearch.RecipeEventListener;
import com.d109.reper.elasticsearch.RecipeSearchRepository;
import com.d109.reper.repository.RecipeRepository;
import com.d109.reper.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final ElasticsearchClient elasticsearchClient;
    private final RecipeEventListener recipeEventListener;
    private final EntityManager em;
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);


    // 레시피 저장
    @Transactional
    public void saveRecipes(List<Recipe> recipes, Long storeId) {
//        logger.info("트랜잭션 시작 - 레시피 저장");
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found for the given storeId.")); // Store 조회

        for (Recipe recipe : recipes) {
//            logger.info("레시피 저장: {}", recipe.getRecipeName());
            // 기존 레시피 삭제
            recipeRepository.deleteByStoreAndRecipeName(storeId, recipe.getRecipeName(), recipe.getType());

            // 새로운 레시피 저장 준비
            recipe.setStore(store);
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

            Recipe savedRecipe = recipeRepository.save(recipe);
//            logger.info("레시피 저장 완료: {}", recipe.getRecipeName());

            // mySQL에 저장된 레시피 Elasticsearch DB에도 저장
            recipeEventListener.saveRecipeToElasticsearch(savedRecipe);
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

        RecipeDocument elasticRecipe = recipeSearchRepository.findByRecipeId(recipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Elasticsearch에 해당 레시피가 정보가 없습니다."));

        recipeRepository.delete(recipeId);
        recipeSearchRepository.delete(elasticRecipe);
    }


    // 가게별 레시피 검색
    public List<RecipeDocument> searchRecipeName(Long storeId, String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        Pageable pageable = PageRequest.of(0, 1000);

        return recipeSearchRepository.searchByStoreIdAndRecipeName(storeId, keyword, pageable);
    }


    // 가게별 레시피에서 재료 포함 검색
    public List<RecipeDocument> searchIncludeIngredient(Long storeId, String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        // 키워드 매핑
        String mappedKeyword = switch (keyword) {
            case "커피" -> "샷";
            case "초콜릿" -> "초코";
            default -> keyword;        // 나머지는 그대로 사용
        };

        Pageable pageable = PageRequest.of(0, 1000);

        return recipeSearchRepository.findByStoreIdAndIngredientsContaining(storeId, mappedKeyword, pageable);
    }


    // 재료 미포함 검색
    public List<RecipeDocument> searchExcludeIngredient(Long storeId, String keyword) {
        try {
            // 키워드 매핑
            String mappedKeyword = switch (keyword) {
                case "커피" -> "샷";
                case "초콜릿" -> "초코";
                default -> keyword;
            };

            SearchResponse<RecipeDocument> response = elasticsearchClient.search(s -> s
                    .index("recipes")
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m.match(t -> t.field("storeId").query(storeId)))
                                    .mustNot(n -> n.wildcard(w -> w.field("ingredients").value("*" + mappedKeyword + "*")))
                            )
                    )
                            .size(1000),
                    RecipeDocument.class
            );

            List<RecipeDocument> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            if (results.isEmpty()) {
                return List.of();
            }

            return results;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
        }
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



