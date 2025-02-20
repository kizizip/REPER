package com.d109.reper.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;

    private String  storeName;

    // 양방향 관계 설정
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreEmployee> storeEmployees = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    //연관관계 메서드
    //가입 요청 메서드 (storeEmployee 추가)
    public void addStoreEmployee(StoreEmployee storeEmployee) {
        storeEmployees.add(storeEmployee);
        if (storeEmployee.getStore() != this) {
            storeEmployee.setStore(this);
        }
    }

    // Store에서 User 설정
    public void setOwner(User user) {
        if (this.owner != null) {
            this.owner.getStores().remove(this);
        }
        this.owner = user;
        if (user != null && !user.getStores().contains(this)) {
            user.getStores().add(this);
        }
    }

}