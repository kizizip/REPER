package com.d109.reper.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserToken {

    @Id
    private Long userId;

    private Long storeId;

    private String token;

}
