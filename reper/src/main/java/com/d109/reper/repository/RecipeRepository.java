package com.d109.reper.repository;

import com.d109.reper.domain.Recipe;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {

    private final EntityManager em;

    //레시피 저장(단건)
    public void save(Recipe recipe) {
        em.persist(recipe);
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

    //레시피 삭제(단건)
    public void delete(Long recipeId) {
        Recipe recipe = em.find(Recipe.class, recipeId);
        if (recipe != null) {
            em.remove(recipe);
        }
    }

}
