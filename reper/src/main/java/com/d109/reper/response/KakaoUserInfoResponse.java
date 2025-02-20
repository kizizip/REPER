package com.d109.reper.response;

public class KakaoUserInfoResponse {
    private Properties properties;
    private KakaoAccount kakao_account;

    // Getters and Setters
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public KakaoAccount getKakao_account() {
        return kakao_account;
    }

    public void setKakao_account(KakaoAccount kakao_account) {
        this.kakao_account = kakao_account;
    }

    public static class KakaoAccount {
        private String email;

        // Getter and Setter
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Properties {
        private String nickname;

        // Getter and Setter
        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
