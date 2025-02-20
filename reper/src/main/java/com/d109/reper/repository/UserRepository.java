package com.d109.reper.repository;

import com.d109.reper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Optional<User> deleteByUserId(Long userId);

    @Query("SELECT u.kakao FROM User u WHERE u.email = :email")
    Optional<Boolean> findKakaoByEmail(@Param("email") String email);

    @Query("SELECT u.google FROM User u where u.email = :email")
    Optional<Boolean> findGoogleByEmail(@Param("email") String email);

}