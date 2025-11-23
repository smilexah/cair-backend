package ai.lab.cair.repository;

import ai.lab.cair.entity.Translation;
import ai.lab.cair.entity.enums.LanguageCode;
import ai.lab.cair.entity.enums.TranslationField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByEntityTypeAndEntityId(String entityType, Long entityId);

    Optional<Translation> findByEntityTypeAndEntityIdAndFieldNameAndLanguageCode(
            String entityType, Long entityId, TranslationField fieldName, LanguageCode languageCode);

    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}

