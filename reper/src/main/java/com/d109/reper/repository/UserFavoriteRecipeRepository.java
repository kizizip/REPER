package com.d109.reper.repository;

import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserFavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, Long> {

    Optional<UserFavoriteRecipe> findByUserAndRecipe(User user, Recipe recipe);
    List<UserFavoriteRecipe> findByUser_UserIdAndRecipe_Store_StoreId(Long userId, Long storeId);
}
