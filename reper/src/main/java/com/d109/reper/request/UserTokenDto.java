package com.d109.reper.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTokenDto {
    private Long userId;
    private Long storeId;
    private String token;

}
