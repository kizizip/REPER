package com.d109.reper.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeSearchRepository extends ElasticsearchRepository<RecipeDocument, Long> {

    // 가게별 레시피 검색
    List<RecipeDocument> findByStoreIdAndRecipeNameContaining(Long storeId, String keyword, Pageable pageable);

    // 가게별 레시피에서 재료 포함 검색
    List<RecipeDocument> findByStoreIdAndIngredientsContaining(Long storeId, String ingredient, Pageable pageable);

    // 재료 미포함 검색
    List<RecipeDocument> findByStoreIdAndIngredientsIsNotContaining(Long storeId, String ingredient);


}


