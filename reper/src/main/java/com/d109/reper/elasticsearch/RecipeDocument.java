package com.d109.reper.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

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

    @Field(type = FieldType.Text)
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
