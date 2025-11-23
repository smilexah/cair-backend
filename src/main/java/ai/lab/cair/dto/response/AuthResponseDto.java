package ai.lab.cair.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
