package com.d109.reper.repository;


import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeType;
import com.d109.reper.elasticsearch.RecipeSearchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {

    private final EntityManager em;

    private final RecipeSearchRepository recipeSearchRepository;

    //레시피 저장(단건)
    public Recipe save(Recipe recipe) {

        em.persist(recipe);

        return recipe; // elasticsearch 저장 위해 객체 반환으로 수정
    }


    //레시피 조회(가게별)
    public List<Recipe> findByStore(Long storeId) {
        return em.createQuery("select r from Recipe r where r.store.storeId = :storeId", Recipe.class)
                .setParameter("storeId", storeId)
                .getResultList();
    }

    //레시피 조회(단건)
    public Recipe findOne(Long recipeId) {
        return em.find(Recipe.class, recipeId);
    }

    public List<Recipe> findAllRecipes() {
        return em.createQuery("select r from Recipe r", Recipe.class)
                .getResultList();
    }

    //레시피 삭제(단건)
    public void delete(Long recipeId) {
        Recipe recipe = em.find(Recipe.class, recipeId);
        if (recipe != null) {
            em.remove(recipe);
        }
    }

    // 특정 가게의 특정 레시피 삭제
    public void deleteByStoreAndRecipeName(Long storeId, String recipeName, RecipeType recipeType) {
        // 해당 레시피 조회
        List<Recipe> recipe = em.createQuery(
                "select r from Recipe  r where r.store.storeId = :storeId and r.recipeName = :recipeName and r.type = :recipeType", Recipe.class)
                .setParameter("storeId", storeId)
                .setParameter("recipeName", recipeName)
                .setParameter("recipeType", recipeType)
                .getResultList();

        if (!recipe.isEmpty()) {
            em.remove(recipe.get(0));
        }
    }


}
