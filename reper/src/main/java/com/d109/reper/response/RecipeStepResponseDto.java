package com.d109.reper.response;

import com.d109.reper.domain.RecipeStep;
import lombok.Getter;

@Getter
public class RecipeStepResponseDto {

    private String animationUrl;
    private String instruction;

    public RecipeStepResponseDto(RecipeStep recipeStep) {
        this.animationUrl = recipeStep.getAnimationUrl();
        this.instruction = recipeStep.getInstruction();
    }
}