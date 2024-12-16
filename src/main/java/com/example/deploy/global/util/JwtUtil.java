package com.example.deploy.global.util;


import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import com.example.deploy.global.exception.GlobalResponse;
import com.example.deploy.global.type.JwtOptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j(topic = "JWT Log")
public class JwtUtil {

    @Value("${jwt.key}") private String secretKey;

    private static final String AUTHORIZATION_ACCESS_HEADER = "accessToken";
    private static final String AUTHORIZATION_REFRESH_HEADER = "refreshToken";
    private static final String BEARER_PREFIX = "Bearer ";

    private final long ACCESS_TOKEN_EXPIRATION_PERIOD = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_EXPIRATION_PERIOD = 24 * 60 * 60 * 1000L; // 1일

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public void jwtExceptionHandler(HttpServletResponse response, AuthErrorCode error) {
        log.warn(error.getMsg());
        response.setStatus(error.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json =
                    new ObjectMapper()
                            .writeValueAsString(
                                    new GlobalResponse(error.getMsg(), error.getStatus()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String getAccessTokenFromRequest(HttpServletRequest req) {
        return getTokenFromCookie(req, AUTHORIZATION_ACCESS_HEADER);
    }

    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        return getTokenFromCookie(req, AUTHORIZATION_REFRESH_HEADER);
    }

    private String getTokenFromCookie(HttpServletRequest req, String authorizationRefreshHeader) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(authorizationRefreshHeader)) {
                    try {
                        return URLDecoder.decode(
                                cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public Claims getUserInfoFromToken(String token, HttpServletResponse response) {
        token = substringToken(token, response);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void accessTokenCookieClear(HttpServletRequest req, HttpServletResponse res) {
        clearCookie(req, res, AUTHORIZATION_ACCESS_HEADER);
    }

    public void refreshTokenCookieClear(HttpServletRequest req, HttpServletResponse res) {
        clearCookie(req, res, AUTHORIZATION_REFRESH_HEADER);
    }

    public void clearCookie(HttpServletRequest req, HttpServletResponse res, String key) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    res.addCookie(cookie);
                }
            }
        }
    }

    public Cookie getAccessTokenCookie(String email, String role, Long userId)
            throws UnsupportedEncodingException {
        return getCookie(
                createAccessToken(email, role, userId),
                AUTHORIZATION_ACCESS_HEADER,
                ACCESS_TOKEN_EXPIRATION_PERIOD);
    }

    public Cookie getRefreshTokenCookie(String email, String role, Long userId)
            throws UnsupportedEncodingException {
        return getCookie(
                createRefreshToken(email, role, userId),
                AUTHORIZATION_REFRESH_HEADER,
                REFRESH_TOKEN_EXPIRATION_PERIOD);
    }

    public boolean validateCookieName(Cookie cookie) {
        return Objects.equals(cookie.getName(), AUTHORIZATION_ACCESS_HEADER)
                || Objects.equals(cookie.getName(), AUTHORIZATION_REFRESH_HEADER);
    }

    public boolean validateToken(String token, HttpServletResponse response) {
        try {
            token = substringToken(token, response);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException
                | MalformedJwtException
                | SignatureException
                | ExpiredJwtException
                | UnsupportedJwtException
                | IllegalArgumentException e) {
            return false;
        }
    }

    private String substringToken(String tokenValue, HttpServletResponse response) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        return "";
    }

    private String createAccessToken(String email, String role, Long userId) {
        Date date = new Date();

        return BEARER_PREFIX
                + Jwts.builder()
                        .setSubject(email)
                        .claim(JwtOptionType.ROLE.name(), role)
                        .claim(JwtOptionType.EMAIL.name(), email)
                        .claim(JwtOptionType.USER_ID.name(), userId)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION_PERIOD))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    private String createRefreshToken(String email, String role, Long userId) {
        Date date = new Date();

        return BEARER_PREFIX
                + Jwts.builder()
                        .setSubject(email)
                        .claim(JwtOptionType.ROLE.name(), role)
                        .claim(JwtOptionType.EMAIL.name(), email)
                        .claim(JwtOptionType.USER_ID.name(), userId)
                        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRATION_PERIOD))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    private Cookie getCookie(
            String token, String authorizationRefreshHeader, long refreshTokenExpirationPeriod)
            throws UnsupportedEncodingException {
        token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
        Cookie cookie = new Cookie(authorizationRefreshHeader, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge((int) refreshTokenExpirationPeriod);

        return cookie;
    }
}
