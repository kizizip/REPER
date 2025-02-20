package com.d109.reper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class RecipeStep {

    @Id @GeneratedValue
    private Long recipeStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private int stepNumber;

    private String instruction;

    private String animationUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
