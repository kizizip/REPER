package com.d109.reper.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "stores_index")
public class StoreDocument {

    @Id
    private Long storeId;

    private String storeName;
}
