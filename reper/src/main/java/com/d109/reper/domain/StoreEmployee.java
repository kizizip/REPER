package com.d109.reper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class StoreEmployee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private boolean isEmployed; // 승인여부

    public void setIsEmployed(boolean isEmployed) {
        this.isEmployed = isEmployed;
    }
}
