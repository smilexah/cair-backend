package ai.lab.cair.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
@OpenAPIDefinition(
        info = @Info(
                title = "CAIR Backend API",
                version = "1.0",
                description = "API documentation for CAIR Backend"
        ),
        servers = {
                @Server(url = "http://localhost:8080/api", description = "Local server"),
                @Server(url = "https://cair-backend-production.up.railway.app/api", description = "Production server")
        }
)

@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT-based authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
