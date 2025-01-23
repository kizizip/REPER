package com.d109.reper.response;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    // 필요시 추가적인 정보나 필드를 포함할 수 있습니다.
}
