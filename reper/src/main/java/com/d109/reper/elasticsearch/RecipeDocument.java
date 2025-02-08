package com.d109.reper.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@Document(indexName = "recipes")
public class RecipeDocument {

    @Id
    private Long recipeId;

    @Field(type = FieldType.Long)
    private Long storeId;

    @Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
    private String recipeName;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String recipeImg;

    @Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
    private List<String> ingredients;

}
