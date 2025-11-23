package ai.lab.cair.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponseDto {
    private Long id;
    private String name;
    private RoleDto role;
    private BioDto bio;
    private String image;
    private List<String> expertise;
    private String email;
    private String linkedin;
    private String github;
    private String scholar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDto {
        private String en;
        private String ru;
        private String kz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BioDto {
        private String en;
        private String ru;
        private String kz;
    }
}

