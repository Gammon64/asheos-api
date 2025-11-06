package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    
    List<Occurrence> findByReportedBy_id(Long id);
    List<Occurrence> findByStatus(OccurrenceStatus status);
}
