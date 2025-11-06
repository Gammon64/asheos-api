package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.gammonsistemas.asheos.domain.occurrence.dto.OccurrenceRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OccurrenceService {
    
    private final OccurrenceRepository occurrenceRepository;
    private final UserService userService;

    public List<Occurrence> findAll() {
        return occurrenceRepository.findAll();
    }

    public List<Occurrence> findByReporter(Long userId) {
        return occurrenceRepository.findByReportedBy_id(userId);
    }

    public Occurrence findById(Long id) {
        return occurrenceRepository.findById(id)
        .orElseThrow(() ->new EntityNotFoundException("A Ocorrência não existe"));
    }

    public Occurrence create (OccurrenceRequest request) {
        User reporter = userService.findById(request.reportedBy());

        Occurrence occurrence = new Occurrence(
            null,
            request.title(),
            request.description(),
            OccurrenceStatus.OPENED,
            reporter
        );

        return occurrenceRepository.save(occurrence);
    }

    public Occurrence update (Long id, OccurrenceStatus status) {
        Occurrence occurrence = findById(id);

        // Regra de negócio: uma ocorrência fechada não pode ser reaberta
        if(occurrence.getStatus().equals(OccurrenceStatus.CLOSED)) throw new IllegalStateException("Ocorrência fechada não pode ser reaberta!");

        occurrence.setStatus(status);
        return occurrenceRepository.save(occurrence);
    }

    public void delete(Long id) {
        if (!occurrenceRepository.existsById(id)) {
            throw new EntityNotFoundException("Ocorrência não encontrada com ID: " + id);
        }
        occurrenceRepository.deleteById(id);
    }
}
