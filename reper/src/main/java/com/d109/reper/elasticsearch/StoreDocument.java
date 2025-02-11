package com.d109.reper.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Document(indexName = "stores_index")
@Setting(settingPath = "recipes-index-settings.json")
public class StoreDocument {

    @Id
    private Long storeId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "nori_edge_ngram_analyzer", searchAnalyzer = "nori_edge_ngram_analyzer")
            }
    )
    private String storeName;
}
