package com.d109.reper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Recipe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String recipeName;

    @Enumerated(EnumType.STRING)
    private RecipeCategory category; // ENUM [COFFEE, NON_COFFEE, ADE, TEA, SMOOTHIE, FRAPPE]

    @Enumerated(EnumType.STRING)
    private RecipeType type; // ENUM [ICE, HOT]

    @Column(length = 2000)
    private String recipeImg;

    private LocalDateTime createdAt;

    // 양방향 관계 설정
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> recipeSteps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFavoriteRecipe> userFavoriteRecipes = new ArrayList<>();

    // 양방향 연관관계 메서드
    public void addRecipeStep(RecipeStep recipeStep) {
        recipeSteps.add(recipeStep);
        recipeStep.setRecipe(this);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }
}
