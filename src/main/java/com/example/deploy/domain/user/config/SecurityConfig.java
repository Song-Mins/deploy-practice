package com.example.deploy.domain.user.config;


import com.example.deploy.domain.auth.client.GoogleApiClient;
import com.example.deploy.domain.auth.client.KakaoApiClient;
import com.example.deploy.domain.auth.client.NaverApiClient;
import com.example.deploy.domain.user.config.filter.GoogleLoginFilter;
import com.example.deploy.domain.user.config.filter.KakaoLoginFilter;
import com.example.deploy.domain.user.config.filter.LoginFilter;
import com.example.deploy.domain.user.config.filter.NaverLoginFilter;
import com.example.deploy.domain.user.config.filter.RequestFilter;
import com.example.deploy.domain.user.exception.AuthException;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import com.example.deploy.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http, AuthenticationConfiguration authenticationConfiguration)
        throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                configurer ->
                    configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(matcher -> matcher
            // /api/admin/** 경로는 ROLE_ADMIN 만 접근 가능
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/login/oauth2/code/kakao").permitAll()
            .requestMatchers(HttpMethod.POST, "/login/oauth2/code/naver").permitAll()
            .requestMatchers(HttpMethod.POST, "/login/oauth2/code/google").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/info/imp").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/sign-up/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/enterpriser/sign-up/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/influencer/sign-up/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/password/change/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/id/find").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/post/notices/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/campaigns/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
            // 그 외 요청은 인증된 사용자만 허용
            .anyRequest().authenticated());

        http.cors(
            httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));

        http.logout(
            logout ->
                logout.logoutUrl("/logout")
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                    .permitAll());

        http.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(requestFilter(), LoginFilter.class);

        http.addFilterBefore(kakaoLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(googleLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(naverLoginFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        // 필터 등록 로그 추가
        System.out.println("LoginFilter registered");
        return filter;
    }

    @Bean
    public KakaoLoginFilter kakaoLoginFilter() throws Exception {
        KakaoLoginFilter filter = new KakaoLoginFilter(jwtUtil, kakaoApiClient);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public GoogleLoginFilter googleLoginFilter() throws Exception {
        GoogleLoginFilter filter = new GoogleLoginFilter(jwtUtil, googleApiClient);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public NaverLoginFilter naverLoginFilter() throws Exception {
        NaverLoginFilter filter = new NaverLoginFilter(jwtUtil, naverApiClient);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter(jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(
            List.of(
                "https://final-frontend-beta.vercel.app",
                "https://g6.dainreview.kr",
                "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
            try {

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (jwtUtil.validateCookieName(cookie)) {
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }

            } catch (Exception e) {
                throw new AuthException(AuthErrorCode.LOGOUT_ERROR);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.sendRedirect("/");
        }
    }
}
