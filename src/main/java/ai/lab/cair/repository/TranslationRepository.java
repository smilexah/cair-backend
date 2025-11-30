package ai.lab.cair.repository;

import ai.lab.cair.entity.Translation;
import ai.lab.cair.entity.enums.LanguageCode;
import ai.lab.cair.entity.enums.TranslationField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByEntityTypeAndEntityId(String entityType, Long entityId);

    Optional<Translation> findByEntityTypeAndEntityIdAndFieldNameAndLanguageCode(
            String entityType, Long entityId, TranslationField fieldName, LanguageCode languageCode);

    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);

    // Batch loading to solve N+1 problem
    @Query("SELECT t FROM Translation t WHERE t.entityType = :entityType AND t.entityId IN :entityIds")
    List<Translation> findByEntityTypeAndEntityIdIn(
            @Param("entityType") String entityType,
            @Param("entityIds") List<Long> entityIds
    );
}

