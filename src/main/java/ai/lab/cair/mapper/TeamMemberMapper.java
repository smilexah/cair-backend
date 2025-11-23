package ai.lab.cair.mapper;

import ai.lab.cair.dto.request.TeamMemberRequestDto;
import ai.lab.cair.dto.response.TeamMemberResponseDto;
import ai.lab.cair.entity.TeamMember;
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
public interface TeamMemberMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    String ENTITY_TYPE = "TeamMember";

    // Basic mappings between DTO and entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expertise", expression = "java(toJson(dto.getExpertise()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TeamMember toEntity(TeamMemberRequestDto dto);

    @Mapping(target = "expertise", expression = "java(toJson(dto.getExpertise()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget TeamMember entity, TeamMemberRequestDto dto);

    // Mapping from entity + translations to response DTO
    @Mapping(target = "role", expression = "java(extractRole(translations))")
    @Mapping(target = "bio", expression = "java(extractBio(translations))")
    @Mapping(target = "expertise", expression = "java(fromJson(entity.getExpertise()))")
    TeamMemberResponseDto toDto(TeamMember entity, List<Translation> translations);

    // Helper methods for translations (MapStruct will call these default methods)
    default List<Translation> createTranslations(Long entityId, TeamMemberRequestDto dto) {
        List<Translation> translations = new ArrayList<>();

        // Role translations
        translations.add(createTranslation(entityId, TranslationField.ROLE, LanguageCode.EN, dto.getRole().getEn()));
        translations.add(createTranslation(entityId, TranslationField.ROLE, LanguageCode.RU, dto.getRole().getRu()));
        translations.add(createTranslation(entityId, TranslationField.ROLE, LanguageCode.KZ, dto.getRole().getKz()));

        // Bio translations
        translations.add(createTranslation(entityId, TranslationField.BIO, LanguageCode.EN, dto.getBio().getEn()));
        translations.add(createTranslation(entityId, TranslationField.BIO, LanguageCode.RU, dto.getBio().getRu()));
        translations.add(createTranslation(entityId, TranslationField.BIO, LanguageCode.KZ, dto.getBio().getKz()));

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

    default TeamMemberResponseDto.RoleDto extractRole(List<Translation> translations) {
        return TeamMemberResponseDto.RoleDto.builder()
                .en(findTranslation(translations, TranslationField.ROLE, LanguageCode.EN))
                .ru(findTranslation(translations, TranslationField.ROLE, LanguageCode.RU))
                .kz(findTranslation(translations, TranslationField.ROLE, LanguageCode.KZ))
                .build();
    }

    default TeamMemberResponseDto.BioDto extractBio(List<Translation> translations) {
        return TeamMemberResponseDto.BioDto.builder()
                .en(findTranslation(translations, TranslationField.BIO, LanguageCode.EN))
                .ru(findTranslation(translations, TranslationField.BIO, LanguageCode.RU))
                .kz(findTranslation(translations, TranslationField.BIO, LanguageCode.KZ))
                .build();
    }

    default String findTranslation(List<Translation> translations, TranslationField field, LanguageCode lang) {
        return translations.stream()
                .filter(t -> t.getFieldName() == field && t.getLanguageCode() == lang)
                .map(Translation::getValue)
                .findFirst()
                .orElse("");
    }

    // JSON helpers for expertise list
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
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to list", e);
        }
    }
}
