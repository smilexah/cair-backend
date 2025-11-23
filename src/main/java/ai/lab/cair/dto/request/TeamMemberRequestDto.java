package ai.lab.cair.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TeamMemberRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotNull(message = "Role is required")
    private RoleDto role;

    @NotNull(message = "Bio is required")
    private BioDto bio;

    private String image;

    @NotNull(message = "Expertise is required")
    @Size(min = 1, message = "At least one expertise is required")
    private List<String> expertise;

    @Email(message = "Invalid email format")
    private String email;

    private String linkedin;

    private String github;

    private String scholar;

    @Data
    public static class RoleDto {
        @NotBlank(message = "Role in English is required")
        private String en;

        @NotBlank(message = "Role in Russian is required")
        private String ru;

        @NotBlank(message = "Role in Kazakh is required")
        private String kz;
    }

    @Data
    public static class BioDto {
        @NotBlank(message = "Bio in English is required")
        private String en;

        @NotBlank(message = "Bio in Russian is required")
        private String ru;

        @NotBlank(message = "Bio in Kazakh is required")
        private String kz;
    }
}

