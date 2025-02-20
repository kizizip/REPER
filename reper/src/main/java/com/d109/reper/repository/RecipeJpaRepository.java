package com.d109.reper.repository;

import com.d109.reper.domain.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeJpaRepository extends JpaRepository<Recipe, Long> {

    // 레시피 검색
    @Query("SELECT r FROM Recipe r WHERE r.store.storeId = :storeId AND r.recipeName LIKE CONCAT('%', :keyword, '%')")
    List<Recipe> searchByStoreIdAndRecipeName(
            @Param("storeId") Long storeId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 재료 포함 검색
    @Query("""
        SELECT r FROM Recipe r
        WHERE r.store.storeId = :storeId
        AND r.recipeId IN (
            SELECT ri.recipe.recipeId
            FROM Ingredient ri
            WHERE ri.ingredientName LIKE CONCAT('%', :keyword, '%')
        )
    """)
    List<Recipe> searchByStoreIdAndIngredient(
            @Param("storeId") Long storeId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
