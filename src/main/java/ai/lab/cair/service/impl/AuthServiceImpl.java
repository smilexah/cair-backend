package ai.lab.cair.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ai.lab.cair.security.properties.JwtProperties;
import ai.lab.cair.dto.request.AuthRequestDto;
import ai.lab.cair.dto.response.AuthResponseDto;
import ai.lab.cair.entity.enums.TokenType;
import ai.lab.cair.exception.UnauthorizedException;
import ai.lab.cair.entity.User;
import ai.lab.cair.service.AuthService;
import ai.lab.cair.service.RedisService;
import ai.lab.cair.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto, HttpServletResponse response) throws BadRequestException {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDto.getUsername(),
                            authRequestDto.getPassword()
                    )
            );
            User user = (User) auth.getPrincipal();

            String accessToken = jwtService.generateToken(user.getUsername(), TokenType.ACCESS_TOKEN);
            String refreshToken = jwtService.generateToken(user.getUsername(), TokenType.REFRESH_TOKEN);

            Cookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken);
            response.addCookie(refreshTokenCookie);

            return buildAuthResponseDto(accessToken);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BadRequestException("Token generation failed", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during login", e);
        }
    }


    @Override
    public AuthResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshCookie = extractRefreshTokenCookie(request);
        String refreshToken = refreshCookie.getValue();

        if (redisService.hasToken(refreshToken) || !jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid or blacklisted refresh token");
        }

        invalidateToken(refreshToken);

        return rotateTokens(refreshToken, response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith(BEARER_PREFIX)) {
            invalidateToken(accessToken.substring(7));
        }

        try {
            Cookie refreshTokenCookie = extractRefreshTokenCookie(request);
            invalidateToken(refreshTokenCookie.getValue());
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException("Refresh token is missing or invalid during logout");
        }
    }

    private AuthResponseDto rotateTokens(String refreshToken, HttpServletResponse response) {
        String username = jwtService.extractSubject(refreshToken);
        String accessToken = jwtService.generateToken(username, TokenType.ACCESS_TOKEN);
        String newRefreshToken = jwtService.generateToken(username, TokenType.REFRESH_TOKEN);

        Cookie newRefreshCookie = buildRefreshTokenCookie(newRefreshToken);
        response.addCookie(newRefreshCookie);

        return buildAuthResponseDto(accessToken);
    }

    private void invalidateToken(String token) {
        try {
            long ttl = (jwtService.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000;
            log.info("Expiration time in seconds : {}", ttl);
            if (ttl > 0) {
                redisService.setTokenWithTTL(token, "blacklisted", ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.warn("Cannot invalidate token. Possibly already expired or malformed: {}", e.getMessage());
        }
    }

    private AuthResponseDto buildAuthResponseDto(String accessToken) {
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType(TokenType.ACCESS_TOKEN.name())
                .expiresIn(jwtProperties.getAccessTokenTtl())
                .build();
    }

    private Cookie buildRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(TokenType.REFRESH_TOKEN.name(), refreshToken);
        refreshTokenCookie.setMaxAge(jwtProperties.getRefreshCookieTtl());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        return refreshTokenCookie;
    }

    private Cookie extractRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnauthorizedException("Refresh token is missing");
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(TokenType.REFRESH_TOKEN.name()))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Refresh token is missing"));
    }
}

