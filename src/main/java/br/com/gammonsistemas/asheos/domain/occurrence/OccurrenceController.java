package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gammonsistemas.asheos.domain.occurrence.dto.OccurrenceRequest;
import br.com.gammonsistemas.asheos.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {

    private final OccurrenceService occurrenceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Occurrence>> getAllOccurrences() {
        List<Occurrence> occurrences = occurrenceService.findAll();

        return ResponseEntity.ok(occurrences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Occurrence> getOccurrenceById(@PathVariable Long id) {
        Occurrence occurrence = occurrenceService.findById(id);
        return ResponseEntity.ok(occurrence);
    }

    @PostMapping
    public ResponseEntity<Occurrence> postOccurrence(@Valid @RequestBody OccurrenceRequest request,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();

        Occurrence occurrence = occurrenceService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(occurrence);
    }

}
