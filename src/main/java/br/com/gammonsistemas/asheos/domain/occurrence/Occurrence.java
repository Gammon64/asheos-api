package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import br.com.gammonsistemas.asheos.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "occurrences")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // TEXT é útil para textos longos
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OccurrenceStatus status = OccurrenceStatus.OPENED; // Valor padrão

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User reportedBy;

    @OneToMany(mappedBy = "occurrence", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Attachment> attachments;
}
