package com.d109.reper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String userName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role; // ENUM[OWNER, STAFF]

    private LocalDateTime createdAt;

    private boolean kakao;

    private boolean google;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 양방향 관계 설정
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Store> stores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreEmployee> storeEmployees = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFavoriteRecipe> userFavoriteRecipes = new ArrayList<>();

    // 양방향 연관관계 메서드
    // User-Store - Store 등록
    public void addStore(Store store) {
        if (this.role != UserRole.OWNER) {
            throw new IllegalStateException("가게 등록은 OWNER 권한이 있는 사용자만 가능합니다.");
        }
        stores.add(store);
        store.setOwner(this);
    }
    // User-StoreEmployee
    public void addStoreEmployee(StoreEmployee storeEmployee) {
        storeEmployees.add(storeEmployee);
        storeEmployee.setUser(this);
    }
    // User - UserFavoriteRecipe
    public void addUserFavoriteRecipe(UserFavoriteRecipe userFavoriteRecipe) {
        userFavoriteRecipes.add(userFavoriteRecipe);
        userFavoriteRecipe.setUser(this);
    }
}
