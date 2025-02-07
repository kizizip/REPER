package com.d109.reper.response;

import com.d109.reper.domain.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StoreResponseDto {
    private Long storeId;
    private String storeName;
    private Long ownerId;

    public StoreResponseDto(Store store) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.ownerId = store.getOwner().getUserId();
    }

}
