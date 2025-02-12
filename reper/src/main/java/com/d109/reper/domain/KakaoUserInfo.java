package com.d109.reper.domain;

public class KakaoUserInfo {
    private String email;
    private String nickname;

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // toString() 메서드 오버라이드
    @Override
    public String toString() {
        return "KakaoUserInfo{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    // 카카오 응답에 맞는 구조로 수정된 클래스
    public static class KakaoAccount {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Properties {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }


}
