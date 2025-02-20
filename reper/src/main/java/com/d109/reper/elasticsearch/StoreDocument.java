package com.d109.reper.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Document(indexName = "stores")
@Setting(settingPath = "recipes-index-settings.json")
public class StoreDocument {

    @Id
    private Long storeId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer"),  // 기본 형태소 분석기
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "nori_edge_ngram_analyzer", searchAnalyzer = "nori_edge_ngram_analyzer"),  // 초성 검색용
                    @InnerField(suffix = "chosung", type = FieldType.Text, analyzer = "chosung_analyzer", searchAnalyzer = "chosung_analyzer")
            }
    )
    private String storeName;
}
