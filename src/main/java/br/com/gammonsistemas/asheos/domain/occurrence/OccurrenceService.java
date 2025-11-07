package br.com.gammonsistemas.asheos.domain.occurrence;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.gammonsistemas.asheos.core.storage.StorageService;
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

    // Attachments
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

    public List<Occurrence> findAll() {
        return occurrenceRepository.findAll();
    }

    public List<Occurrence> findByReporter(Long userId) {
        return occurrenceRepository.findByReportedBy_id(userId);
    }

    public Occurrence findById(Long id) {
        return occurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("A Ocorrência não existe"));
    }

    public Occurrence create(OccurrenceRequest request, Long userId) {
        User reporter = userService.findById(userId);

        Occurrence occurrence = new Occurrence(
                null,
                request.title(),
                request.description(),
                OccurrenceStatus.OPENED,
                reporter,
                null);

        return occurrenceRepository.save(occurrence);
    }

    public Occurrence update(Long id, OccurrenceStatus status) {
        Occurrence occurrence = findById(id);

        // Regra de negócio: uma ocorrência fechada não pode ser reaberta
        if (occurrence.getStatus().equals(OccurrenceStatus.CLOSED))
            throw new IllegalStateException("Ocorrência fechada não pode ser reaberta!");

        occurrence.setStatus(status);
        return occurrenceRepository.save(occurrence);
    }

    public void delete(Long id) {
        if (!occurrenceRepository.existsById(id)) {
            throw new EntityNotFoundException("Ocorrência não encontrada com ID: " + id);
        }
        occurrenceRepository.deleteById(id);
    }

    // Attachments

    public Attachment findAttachmentById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Anexo não encontrado com ID: " + id));
    }

    /**
     * Adiciona um anexo a uma ocorrência existente.
     *
     * @param occurrenceId O ID da ocorrência.
     * @param file         O arquivo enviado.
     * @param userId       O id dousuário autenticado (para verificação de
     *                     permissão).
     * @return Os metadados do anexo salvo.
     */
    public Attachment addAttachment(Long occurrenceId, MultipartFile file, Long userId) {
        Occurrence occurrence = findById(occurrenceId);

        // Regra de negócio: O usuário só pode anexar se ele for o dono da ocorrência
        handleOccurrenceReporter(occurrence, userId);

        // Define o nome original e a extensão
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Formato: occurrences/[ID_DA_OCORRENCIA]/[UUID_ALEATORIO].[EXTENSAO]
        String objectKey = String.format("occurrences/%d/%s%s",
                occurrenceId,
                UUID.randomUUID().toString(),
                fileExtension);

        // Faz o upload para o S3
        storageService.uploadFile(file, objectKey);

        // Criar a entidade no banco de dados
        Attachment attachment = new Attachment();
        attachment.setOccurrence(occurrence);
        attachment.setFileName(originalFilename);
        attachment.setFilePath(objectKey); // Salva o caminho do S3
        attachment.setMimeType(file.getContentType());

        return attachmentRepository.save(attachment);
    }

    /**
     * Lista todos os anexos de uma ocorrência.
     */
    public List<Attachment> listAttachments(Long occurrenceId, Long userId) {
        Occurrence occurrence = findById(occurrenceId);
        handleOccurrenceReporter(occurrence, userId);

        return attachmentRepository.findAllByOccurrence_Id(occurrenceId);
    }

    /**
     * Busca os bytes de um anexo para download.
     */
    public byte[] downloadAttachment(Long occurrenceId, Long userId, Long attachmentId) {
        Occurrence occurrence = findById(occurrenceId);
        handleOccurrenceReporter(occurrence, userId);

        Attachment attachment = findAttachmentById(attachmentId);

        if (!attachment.getOccurrence().getId().equals(occurrenceId)) {
            throw new AccessDeniedException("Anexo não pertence à ocorrência especificada.");
        }

        return storageService.downloadFile(attachment.getFilePath());
    }

    /**
     * Exclui um anexo do banco e do storage.
     */
    public void deleteAttachment(Long occurrenceId, Long userId, Long attachmentId) {
        Occurrence occurrence = findById(occurrenceId);
        handleOccurrenceReporter(occurrence, userId);

        Attachment attachment = findAttachmentById(attachmentId);

        if (!attachment.getOccurrence().getId().equals(occurrenceId)) {
            throw new AccessDeniedException("Anexo não pertence à ocorrência especificada.");
        }

        // Exclui do Storage
        storageService.deleteFile(attachment.getFilePath());

        // Exclui do banco de dados
        attachmentRepository.delete(attachment);
    }

    /**
     * Verifica se o usuário autenticado é o dono da ocorrência.
     * 
     * @param occurrence
     * @param userId
     */
    private void handleOccurrenceReporter(Occurrence occurrence, Long userId) {
        if (!occurrence.getReportedBy().getId().equals(userId)) {
            throw new AccessDeniedException("Usuário não autorizado a anexar arquivos nesta ocorrência.");
        }
    }
}
