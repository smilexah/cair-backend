package ai.lab.cair.service.jwt;

import ai.lab.cair.security.properties.RsaKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KeyGeneratorService {
    private static final String RSA = "RSA";
    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    private final RsaKeyProperties rsaKeyProperties;

    public PublicKey generatePublicKey() {
        return (PublicKey) generateKey(
                rsaKeyProperties.getPublicKey(),
                BEGIN_PUBLIC_KEY,
                END_PUBLIC_KEY,
                X509EncodedKeySpec.class
        );
    }

    public PrivateKey generatePrivateKey() {
        return (PrivateKey) generateKey(
                rsaKeyProperties.getPrivateKey(),
                BEGIN_PRIVATE_KEY,
                END_PRIVATE_KEY,
                PKCS8EncodedKeySpec.class
        );
    }

    private Key generateKey(Resource resource, String beginMarker, String endMarker, Class<? extends KeySpec> keySpecClass) {
        if (Objects.isNull(resource) || !resource.exists()) {
            throw new IllegalArgumentException("Key resource not found: " + resource);
        }

        try {
            String keyString = Files.readString(Paths.get(resource.getURI()));
            String cleanedKey = keyString
                    .replace(beginMarker, "")
                    .replace(endMarker, "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);

            if (keySpecClass.equals(PKCS8EncodedKeySpec.class)) {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            } else if (keySpecClass.equals(X509EncodedKeySpec.class)) {
                return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            } else {
                throw new IllegalArgumentException("Unsupported KeySpec class: " + keySpecClass.getName());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating key from resource: " + resource, e);
        }
    }
}