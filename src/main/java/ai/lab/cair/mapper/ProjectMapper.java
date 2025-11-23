package ai.lab.cair.mapper;

import ai.lab.cair.dto.request.ProjectRequestDto;
import ai.lab.cair.dto.response.ProjectResponseDto;
import ai.lab.cair.entity.Project;
import ai.lab.cair.entity.Translation;
import ai.lab.cair.entity.enums.LanguageCode;
import ai.lab.cair.entity.enums.TranslationField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    String ENTITY_TYPE = "Project";

    // Basic mappings between DTO and entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", expression = "java(toJson(dto.getTags()))")
    @Mapping(target = "team", expression = "java(toJson(dto.getTeam()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(ProjectRequestDto dto);

    @Mapping(target = "tags", expression = "java(toJson(dto.getTags()))")
    @Mapping(target = "team", expression = "java(toJson(dto.getTeam()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Project entity, ProjectRequestDto dto);

    // Mapping from entity + translations to response DTO
    @Mapping(target = "title", expression = "java(extractTitle(translations))")
    @Mapping(target = "shortDescription", expression = "java(extractShortDescription(translations))")
    @Mapping(target = "fullDescription", expression = "java(extractFullDescription(translations))")
    @Mapping(target = "objectives", expression = "java(extractObjectives(translations))")
    @Mapping(target = "results", expression = "java(extractResults(translations))")
    @Mapping(target = "tags", expression = "java(fromJson(entity.getTags()))")
    @Mapping(target = "team", expression = "java(fromJson(entity.getTeam()))")
    ProjectResponseDto toDto(Project entity, List<Translation> translations);

    // Helper methods for translations
    default List<Translation> createTranslations(Long entityId, ProjectRequestDto dto) {
        List<Translation> translations = new ArrayList<>();

        // Title translations
        translations.add(createTranslation(entityId, TranslationField.TITLE, LanguageCode.EN, dto.getTitle().getEn()));
        translations.add(createTranslation(entityId, TranslationField.TITLE, LanguageCode.RU, dto.getTitle().getRu()));
        translations.add(createTranslation(entityId, TranslationField.TITLE, LanguageCode.KZ, dto.getTitle().getKz()));

        // Short description translations
        translations.add(createTranslation(entityId, TranslationField.SHORT_DESCRIPTION, LanguageCode.EN, dto.getShortDescription().getEn()));
        translations.add(createTranslation(entityId, TranslationField.SHORT_DESCRIPTION, LanguageCode.RU, dto.getShortDescription().getRu()));
        translations.add(createTranslation(entityId, TranslationField.SHORT_DESCRIPTION, LanguageCode.KZ, dto.getShortDescription().getKz()));

        // Full description translations
        translations.add(createTranslation(entityId, TranslationField.FULL_DESCRIPTION, LanguageCode.EN, dto.getFullDescription().getEn()));
        translations.add(createTranslation(entityId, TranslationField.FULL_DESCRIPTION, LanguageCode.RU, dto.getFullDescription().getRu()));
        translations.add(createTranslation(entityId, TranslationField.FULL_DESCRIPTION, LanguageCode.KZ, dto.getFullDescription().getKz()));

        // Objectives translations (stored as JSON arrays)
        translations.add(createTranslation(entityId, TranslationField.OBJECTIVES, LanguageCode.EN, toJson(dto.getObjectives().getEn())));
        translations.add(createTranslation(entityId, TranslationField.OBJECTIVES, LanguageCode.RU, toJson(dto.getObjectives().getRu())));
        translations.add(createTranslation(entityId, TranslationField.OBJECTIVES, LanguageCode.KZ, toJson(dto.getObjectives().getKz())));

        // Results translations (stored as JSON arrays) - only if provided
        if (dto.getResults() != null) {
            if (dto.getResults().getEn() != null) {
                translations.add(createTranslation(entityId, TranslationField.RESULTS, LanguageCode.EN, toJson(dto.getResults().getEn())));
            }
            if (dto.getResults().getRu() != null) {
                translations.add(createTranslation(entityId, TranslationField.RESULTS, LanguageCode.RU, toJson(dto.getResults().getRu())));
            }
            if (dto.getResults().getKz() != null) {
                translations.add(createTranslation(entityId, TranslationField.RESULTS, LanguageCode.KZ, toJson(dto.getResults().getKz())));
            }
        }

        return translations;
    }

    default Translation createTranslation(Long entityId, TranslationField field, LanguageCode lang, String value) {
        return Translation.builder()
                .entityType(ENTITY_TYPE)
                .entityId(entityId)
                .fieldName(field)
                .languageCode(lang)
                .value(value)
                .build();
    }

    default ProjectResponseDto.TitleDto extractTitle(List<Translation> translations) {
        return ProjectResponseDto.TitleDto.builder()
                .en(findTranslation(translations, TranslationField.TITLE, LanguageCode.EN))
                .ru(findTranslation(translations, TranslationField.TITLE, LanguageCode.RU))
                .kz(findTranslation(translations, TranslationField.TITLE, LanguageCode.KZ))
                .build();
    }

    default ProjectResponseDto.ShortDescriptionDto extractShortDescription(List<Translation> translations) {
        return ProjectResponseDto.ShortDescriptionDto.builder()
                .en(findTranslation(translations, TranslationField.SHORT_DESCRIPTION, LanguageCode.EN))
                .ru(findTranslation(translations, TranslationField.SHORT_DESCRIPTION, LanguageCode.RU))
                .kz(findTranslation(translations, TranslationField.SHORT_DESCRIPTION, LanguageCode.KZ))
                .build();
    }

    default ProjectResponseDto.FullDescriptionDto extractFullDescription(List<Translation> translations) {
        return ProjectResponseDto.FullDescriptionDto.builder()
                .en(findTranslation(translations, TranslationField.FULL_DESCRIPTION, LanguageCode.EN))
                .ru(findTranslation(translations, TranslationField.FULL_DESCRIPTION, LanguageCode.RU))
                .kz(findTranslation(translations, TranslationField.FULL_DESCRIPTION, LanguageCode.KZ))
                .build();
    }

    default ProjectResponseDto.ObjectivesDto extractObjectives(List<Translation> translations) {
        return ProjectResponseDto.ObjectivesDto.builder()
                .en(fromJson(findTranslation(translations, TranslationField.OBJECTIVES, LanguageCode.EN)))
                .ru(fromJson(findTranslation(translations, TranslationField.OBJECTIVES, LanguageCode.RU)))
                .kz(fromJson(findTranslation(translations, TranslationField.OBJECTIVES, LanguageCode.KZ)))
                .build();
    }

    default ProjectResponseDto.ResultsDto extractResults(List<Translation> translations) {
        String enResults = findTranslation(translations, TranslationField.RESULTS, LanguageCode.EN);
        String ruResults = findTranslation(translations, TranslationField.RESULTS, LanguageCode.RU);
        String kzResults = findTranslation(translations, TranslationField.RESULTS, LanguageCode.KZ);

        return ProjectResponseDto.ResultsDto.builder()
                .en(enResults.isEmpty() ? null : fromJson(enResults))
                .ru(ruResults.isEmpty() ? null : fromJson(ruResults))
                .kz(kzResults.isEmpty() ? null : fromJson(kzResults))
                .build();
    }

    default String findTranslation(List<Translation> translations, TranslationField field, LanguageCode lang) {
        return translations.stream()
                .filter(t -> t.getFieldName() == field && t.getLanguageCode() == lang)
                .map(Translation::getValue)
                .findFirst()
                .orElse("");
    }

    // JSON helpers for lists
    default String toJson(List<String> list) {
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }
    }

    default List<String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to list", e);
        }
    }
}
