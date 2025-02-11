package com.d109.reper.service;

import com.d109.reper.domain.UserToken;
import com.d109.reper.repository.UserTokenRepository;
import com.d109.reper.request.UserTokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserTokenService {

    @Autowired
    private UserTokenRepository userTokenRepository;

    /**
     * 유저 토큰을 저장하는 메서드
     * @param userTokenDto - 유저 토큰 DTO
     */
    @Transactional
    public void saveUserToken(UserTokenDto userTokenDto) {
        Optional<UserToken> existingUserToken = userTokenRepository.findByUserId(userTokenDto.getUserId());

        if (existingUserToken.isPresent()) {
            // 이미 존재하는 UserToken이 있으면 업데이트
            UserToken userToken = existingUserToken.get();

            // 토큰이나 storeId가 다르면 업데이트
            boolean updated = false;
            if (!userToken.getToken().equals(userTokenDto.getToken())) {
                userToken.setToken(userTokenDto.getToken());
                updated = true;
            }

            if (!userToken.getStoreId().equals(userTokenDto.getStoreId())) {
                userToken.setStoreId(userTokenDto.getStoreId());
                updated = true;
            }

            if (updated) {
                userTokenRepository.save(userToken);  // 수정된 경우에만 저장
            }
        } else {
            // 데이터가 없으면 새로 생성하여 저장
            UserToken newUserToken = new UserToken();
            newUserToken.setUserId(userTokenDto.getUserId());
            newUserToken.setStoreId(userTokenDto.getStoreId());
            newUserToken.setToken(userTokenDto.getToken());

            userTokenRepository.save(newUserToken);  // 새로 저장
        }
    }

    /**
     * storeId에 해당하는 직원들의 FCM 토큰 리스트를 반환하는 메서드
     */
    public List<String> getTokensForStore(long storeId) {
        List<UserToken> tokens = userTokenRepository.findByStoreId(storeId);
        return tokens.stream()
                .map(UserToken::getToken)
                .collect(Collectors.toList());
    }

    /**
     * userId에 해당하는 사용자의 FCM 토큰을 반환하는 메서드
     */
    public String getTokenForUser(Long userId) {
        Optional<UserToken> userToken = userTokenRepository.findByUserId(userId);
        return userToken.map(UserToken::getToken).orElse(null);
    }

    @Transactional
    public void deleteTokensForStore(long storeId) {
        List<UserToken> tokens = userTokenRepository.findByStoreId(storeId);
        if (tokens != null && !tokens.isEmpty()) {
            userTokenRepository.deleteAll(tokens);  // storeId에 해당하는 모든 토큰 삭제
        }
    }

    /**
     * userId에 해당하는 사용자의 FCM 토큰을 삭제하는 메서드
     */
    @Transactional
    public void deleteTokenForUser(Long userId) {
        Optional<UserToken> userToken = userTokenRepository.findByUserId(userId);
        userToken.ifPresent(userTokenRepository::delete);  // userId에 해당하는 토큰 삭제
    }
}
