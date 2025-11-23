package ai.lab.cair.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyProperties {
    private Resource privateKey;
    private Resource publicKey;
}