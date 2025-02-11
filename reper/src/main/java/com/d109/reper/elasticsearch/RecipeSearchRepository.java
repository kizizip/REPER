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
                { 
                  "match": { 
                    "recipeName": { 
                      "query": "?1",
                      "fuzziness": "AUTO"
                    }
                  }
                },        
                { 
                  "match": { 
                    "recipeName.ngram": { 
                      "query": "?1",
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


