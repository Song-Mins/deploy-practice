package com.example.deploy.domain.user.config.filter;

import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.domain.user.exception.AuthException;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import com.example.deploy.domain.user.model.request.LoginRequest;
import com.example.deploy.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public LoginFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            LoginRequest loginRequest =
                    new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            return getAuthenticationManager()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.email(), loginRequest.password(), null));
        } catch (IOException e) {
            throw new AuthException(AuthErrorCode.LOGIN_ERROR);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException {
        CustomUserDetails userDetails = ((CustomUserDetails) authResult.getPrincipal());
        String email = userDetails.getEmail();
        String role = userDetails.getUserRole().name();
        Long userId = userDetails.getUserId();

        response.addCookie(jwtUtil.getAccessTokenCookie(email, role, userId));
        response.addCookie(jwtUtil.getRefreshTokenCookie(email, role, userId));

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        writer.flush();
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) {

        jwtUtil.jwtExceptionHandler(response, AuthErrorCode.LOGIN_ERROR);
    }
}
