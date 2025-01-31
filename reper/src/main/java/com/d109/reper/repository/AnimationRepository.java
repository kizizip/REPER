package com.d109.reper.repository;

import com.d109.reper.domain.Animation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class AnimationRepository {

    private final EntityManager em;

    public void save(Animation animation) {
        em.persist(animation);
    }

    public List<Animation> findAll() {
        return em.createQuery("select a from Animation a", Animation.class)
                .getResultList();
    }
}

