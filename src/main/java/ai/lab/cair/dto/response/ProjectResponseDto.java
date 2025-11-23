package ai.lab.cair.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String slug;
    private TitleDto title;
    private ShortDescriptionDto shortDescription;
    private FullDescriptionDto fullDescription;
    private String image;
    private List<String> tags;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> team;
    private ObjectivesDto objectives;
    private ResultsDto results;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TitleDto {
        private String en;
        private String ru;
        private String kz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortDescriptionDto {
        private String en;
        private String ru;
        private String kz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullDescriptionDto {
        private String en;
        private String ru;
        private String kz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObjectivesDto {
        private List<String> en;
        private List<String> ru;
        private List<String> kz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultsDto {
        private List<String> en;
        private List<String> ru;
        private List<String> kz;
    }
}

