package com.d109.reper.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "recipes")
@Setting(settingPath = "recipes-index-settings.json")
public class RecipeDocument {

    @Id
    private Long recipeId;

    @Field(type = FieldType.Long)
    private Long storeId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer"),  // 기본 형태소 분석기
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "nori_edge_ngram_analyzer", searchAnalyzer = "nori_edge_ngram_analyzer"), // 초성 검색용
                    @InnerField(suffix = "chosung", type = FieldType.Text, analyzer = "chosung_analyzer", searchAnalyzer = "chosung_analyzer")  // 초성 분석기
            }
    )
    private String recipeName;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String recipeImg;

    @Transient
    private Boolean likedRecipe;

    @Field(type = FieldType.Text)
    private List<String> ingredients;

}
