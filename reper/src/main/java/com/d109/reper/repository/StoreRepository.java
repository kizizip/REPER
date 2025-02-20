package com.d109.reper.repository;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByStoreNameAndOwner(String storeName, User owner);
    @Query("select se from StoreEmployee se where se.store.storeId = :storeId")
    List<StoreEmployee> findAllEmployeesByStoreId(@Param("storeId") Long storeId);
    List<Store> findAllByOwner(User owner);
}