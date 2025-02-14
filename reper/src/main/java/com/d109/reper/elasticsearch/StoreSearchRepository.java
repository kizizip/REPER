package com.d109.reper.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long> {

    @Query("""
    {
      "bool": {
        "should": [
          { 
            "match": { 
              "storeName": { 
                "query": "?0",
                "fuzziness": "AUTO"
              }
            }
          },        
          { 
            "match": { 
              "storeName.ngram": { 
                "query": "?0"
              }
            }
          },
          { 
            "match": { 
              "storeName.chosung": { 
                "query": "?0"
              }
            }
          }
        ]
      }
    }
    """)
    List<StoreDocument> findByStoreName(String storeName, Pageable pageable);


    Optional<StoreDocument> findByStoreId(Long storeId);
}
