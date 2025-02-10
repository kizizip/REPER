package com.d109.reper.controller;

import com.d109.reper.response.FavoriteResponseDto;
import com.d109.reper.service.UserFavoriteRecipteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserFavoriteRecipeController {

    private final UserFavoriteRecipteService userFavoriteRecipteService;

    // 즐겨찾는 레시피 등록
    @PostMapping("/user/{userId}/favorites/recipe/{recipeId}")
    @Operation(summary = "즐겨찾는 레시피 등록")
    public ResponseEntity<String> addFavorite(@PathVariable Long userId, @PathVariable Long recipeId) {
        userFavoriteRecipteService.addFavorite(userId, recipeId);
        return ResponseEntity.ok("Added to favorites.");
    }


    // 즐겨찾는 레시피 조회
    @GetMapping("/store/{storeId}/user/{userId}/favorites")
    @Operation(summary = "즐겨찾는 레시피 조회")
    public ResponseEntity<List<FavoriteResponseDto>> getFavorite(@PathVariable Long storeId, @PathVariable Long userId) {
        List<FavoriteResponseDto> response = userFavoriteRecipteService.getFavorite(storeId, userId);
        return ResponseEntity.ok(response);
    }


    // 즐겨찾는 레시피 삭제
    @DeleteMapping("/user/{userId}/favorites/recipe/{recipeId}")
    @Operation(summary = "즐겨찾는 레시피 삭제")
    public ResponseEntity<String> deleteFavorite(@PathVariable Long userId, @PathVariable Long recipeId) {
        userFavoriteRecipteService.deleteFavorite(userId, recipeId);
        return ResponseEntity.ok("Recipe deleted from favorites successfully.");
    }
}
