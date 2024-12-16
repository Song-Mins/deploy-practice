package com.example.deploy.domain.user.config.filter;


import com.example.deploy.domain.auth.client.NaverApiClient;
import com.example.deploy.domain.auth.model.NaverUserInfo;
import com.example.deploy.domain.auth.model.TokenResponse;
import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import com.example.deploy.global.util.JwtUtil;
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
public class NaverLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final NaverApiClient naverApiClient;

    public NaverLoginFilter(JwtUtil jwtUtil, NaverApiClient naverApiClient) {
        this.jwtUtil = jwtUtil;
        this.naverApiClient = naverApiClient;
        setFilterProcessesUrl("/login/oauth2/code/naver");
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        TokenResponse token = naverApiClient.getNaverToken(request.getParameter("code"));
        NaverUserInfo info = naverApiClient.getNaverUserInfo(token.getAccessToken());
        log.info(info.getEmail() + " 로그인 시도");

        return getAuthenticationManager()
                .authenticate(
                        new UsernamePasswordAuthenticationToken(info.getEmail(), "OAuth", null));
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
