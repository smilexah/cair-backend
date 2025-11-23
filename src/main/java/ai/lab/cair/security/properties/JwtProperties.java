package ai.lab.cair.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private long accessTokenTtl;
    private long refreshTokenTtl;
    private int refreshCookieTtl;
}