package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        Long userId = handleLoggedUserId(authentication);

        Occurrence occurrence = occurrenceService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(occurrence);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Occurrence> patchOccurrenceStatus(@PathVariable Long id,
            @RequestParam OccurrenceStatus status) {
        Occurrence occurrence = occurrenceService.update(id, status);
        return ResponseEntity.ok(occurrence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccurrence(@PathVariable Long id) {
        occurrenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Rotas de Anexos

    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Attachment> uploadAttachment(
            @PathVariable Long id,
            @RequestParam MultipartFile file, // O nome do campo no form-data deve ser "file"
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);

        Attachment attachment = occurrenceService.addAttachment(id, file, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }

    /**
     * Lista todos os anexos de uma ocorrência.
     */
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<Attachment>> listAttachments(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);

        List<Attachment> attachments = occurrenceService.listAttachments(id, userId);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Faz o download de um anexo específico.
     */
    @GetMapping("/{id}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);

        byte[] fileData = occurrenceService.downloadAttachment(id, userId, attachmentId);

        Attachment attachment = occurrenceService.findAttachmentById(attachmentId);
        String contentType = attachment.getMimeType();
        String originalFilename = attachment.getFileName();

        // Empacota os bytes em um Recurso
        ByteArrayResource resource = new ByteArrayResource(fileData);

        // Configura os Headers da Resposta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .body(resource);
    }

    /**
     * Deleta um anexo específico.
     */
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);
        occurrenceService.deleteAttachment(id, userId, attachmentId);

        return ResponseEntity.noContent().build();
    }

    private Long handleLoggedUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername()).getId();
    }

}
