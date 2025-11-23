package ai.lab.cair.entity;

import ai.lab.cair.entity.enums.LanguageCode;
import ai.lab.cair.entity.enums.TranslationField;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "translations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entity_type", "entity_id", "field_name", "language_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private String entityType; // e.g., "TeamMember"

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "field_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private TranslationField fieldName;

    @Column(name = "language_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageCode languageCode;

    @Column(name = "value", columnDefinition = "TEXT", nullable = false)
    private String value;
}

