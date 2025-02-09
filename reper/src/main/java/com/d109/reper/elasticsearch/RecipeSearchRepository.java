package com.d109.reper.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeSearchRepository extends ElasticsearchRepository<RecipeDocument, Long> {

    // 가게별 레시피 검색
    @Query("""
    {
      "bool": {
        "must": [
          { "term": { "storeId": "?0" }},
          { 
            "bool": {
              "should": [
                { "match": { "recipeName.nori": "?1" }},          // 형태소 분석 검색
                { "match": { "recipeName.edge_ngram": "?1" }},    // 초성 및 부분 문자열 검색
                { 
                  "fuzzy": { 
                    "recipeName.nori": { 
                      "value": "?1",
                      "fuzziness": "AUTO"
                    }
                  }
                }
              ]
            }
          }
        ]
      }
    }
    """)
    List<RecipeDocument> searchByStoreIdAndRecipeName(Long storeId, String recipeName, Pageable pageable);

    List<RecipeDocument> findByStoreIdAndRecipeNameContaining(Long storeId, String keyword, Pageable pageable);

    // 가게별 레시피에서 재료 포함 검색
    List<RecipeDocument> findByStoreIdAndIngredientsContaining(Long storeId, String ingredient, Pageable pageable);

    Optional<RecipeDocument> findByRecipeId(Long recipeId);

}


