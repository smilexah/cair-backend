package ai.lab.cair.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectRequestDto {
    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    private String slug;

    @NotNull(message = "Title is required")
    private TitleDto title;

    @NotNull(message = "Short description is required")
    private ShortDescriptionDto shortDescription;

    @NotNull(message = "Full description is required")
    private FullDescriptionDto fullDescription;

    private String image;

    @NotNull(message = "Tags are required")
    @Size(min = 1, message = "At least one tag is required")
    private List<String> tags;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(active|completed|upcoming)$", message = "Status must be 'active', 'completed', or 'upcoming'")
    private String status;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Team is required")
    @Size(min = 1, message = "At least one team member is required")
    private List<String> team; // Team member IDs

    @NotNull(message = "Objectives are required")
    private ObjectivesDto objectives;

    private ResultsDto results;

    @Data
    public static class TitleDto {
        @NotBlank(message = "Title in English is required")
        private String en;

        @NotBlank(message = "Title in Russian is required")
        private String ru;

        @NotBlank(message = "Title in Kazakh is required")
        private String kz;
    }

    @Data
    public static class ShortDescriptionDto {
        @NotBlank(message = "Short description in English is required")
        private String en;

        @NotBlank(message = "Short description in Russian is required")
        private String ru;

        @NotBlank(message = "Short description in Kazakh is required")
        private String kz;
    }

    @Data
    public static class FullDescriptionDto {
        @NotBlank(message = "Full description in English is required")
        private String en;

        @NotBlank(message = "Full description in Russian is required")
        private String ru;

        @NotBlank(message = "Full description in Kazakh is required")
        private String kz;
    }

    @Data
    public static class ObjectivesDto {
        @NotNull(message = "Objectives in English are required")
        @Size(min = 1, message = "At least one objective in English is required")
        private List<String> en;

        @NotNull(message = "Objectives in Russian are required")
        @Size(min = 1, message = "At least one objective in Russian is required")
        private List<String> ru;

        @NotNull(message = "Objectives in Kazakh are required")
        @Size(min = 1, message = "At least one objective in Kazakh is required")
        private List<String> kz;
    }

    @Data
    public static class ResultsDto {
        private List<String> en;
        private List<String> ru;
        private List<String> kz;
    }
}

