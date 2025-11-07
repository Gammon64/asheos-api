package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAllByOccurrence_Id(Long occurrenceId);
    
}
