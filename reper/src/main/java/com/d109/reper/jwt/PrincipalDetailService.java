//package com.d109.reper.jwt;
//
//import com.d109.reper.domain.User;
//import com.d109.reper.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PrincipalDetailService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        System.out.println("PrincipalDetailsService : 진입");
//        User user = userRepository.findByEmail(email).orElseThrow();
//
//        return new PrincipalDetails(user);
//    }
//}