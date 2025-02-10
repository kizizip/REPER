package com.d109.reper.service;

import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserFavoriteRecipe;
import com.d109.reper.repository.*;
import com.d109.reper.response.FavoriteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFavoriteRecipteService {

    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final StoreRepository storeRepository;

    // 즐겨찾는 레시피 등록
    @Transactional
    public void addFavorite(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Recipe recipe = recipeRepository.findOne(recipeId);
        if (recipe == null) {
            throw new NoSuchElementException("Recipe not found");
        }

        Store store = recipe.getStore();
        boolean isEmployee = storeEmployeeRepository.existsByStoreAndUser(store, user);
        if (!isEmployee && (store.getOwner() != user)) {
            throw new IllegalArgumentException("Only employees of this store can favorite its recipes.");
        }

        if (userFavoriteRecipeRepository.findByUserAndRecipe(user, recipe).isPresent()) {
            throw new IllegalArgumentException("The recipe is already in the user's favorite list.");
        }

        UserFavoriteRecipe userFavoriteRecipe = new UserFavoriteRecipe();
        userFavoriteRecipe.setUser(user);
        userFavoriteRecipe.setRecipe(recipe);

        userFavoriteRecipeRepository.save(userFavoriteRecipe);
    }


    // 즐겨찾는 레시피 조회
    public List<FavoriteResponseDto> getFavorite(Long storeId, Long userId) {

        storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<UserFavoriteRecipe> userFavoriteRecipes = userFavoriteRecipeRepository.findByUser_UserIdAndRecipe_Store_StoreId(userId, storeId);

        return userFavoriteRecipes.stream()
                .map(favorite -> new FavoriteResponseDto(favorite.getRecipe()))
                .collect(Collectors.toList());
    }


    // 즐겨찾는 레시피 삭제
    @Transactional
    public void deleteFavorite(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Recipe recipe = recipeRepository.findOne(recipeId);
        if (recipe == null) {
            throw new NoSuchElementException("Recipe not found");
        }

        UserFavoriteRecipe userFavoriteRecipe = userFavoriteRecipeRepository.findByUserAndRecipe(user, recipe)
                        .orElseThrow(() -> new NoSuchElementException("The recipe is not in the user's favorite list."));

        userFavoriteRecipeRepository.delete(userFavoriteRecipe);
    }
}
