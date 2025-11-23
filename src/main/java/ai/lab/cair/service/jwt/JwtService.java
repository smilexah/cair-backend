package ai.lab.cair.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ai.lab.cair.security.properties.JwtProperties;
import ai.lab.cair.entity.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final KeyGeneratorService keyGeneratorService;
    private final JwtProperties jwtProperties;

    public String generateToken(String email, TokenType tokenType) {
        long expirationMillis = switch (tokenType) {
            case ACCESS_TOKEN -> jwtProperties.getAccessTokenTtl();
            case REFRESH_TOKEN -> jwtProperties.getRefreshTokenTtl();
        };

        var expirationTime = System.currentTimeMillis() + expirationMillis;
        var expirationDate = new Date(expirationTime);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .claim("type", tokenType)
                .signWith(keyGeneratorService.generatePrivateKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(keyGeneratorService.generatePublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public TokenType extractTokenType(String token) {
        String type = (String) extractClaims(token).get("type");
        return TokenType.valueOf(type);
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            return extractExpiration(jwtToken).after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}