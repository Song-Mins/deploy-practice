package com.example.deploy.domain.user.config.service;


import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.domain.user.exception.AuthException;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.LOGIN_ERROR));

        return new CustomUserDetails(
                user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
