package br.com.gammonsistemas.asheos.domain.occurrence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import br.com.gammonsistemas.asheos.core.storage.StorageService;
import br.com.gammonsistemas.asheos.domain.occurrence.dto.OccurrenceRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import br.com.gammonsistemas.asheos.domain.user.UserService;

@ExtendWith(MockitoExtension.class)
public class OccurrenceServiceTest {

    @Mock
    private OccurrenceRepository occurrenceRepository;

    @Mock
    private UserService userService;

    @Mock
    private StorageService storageService;

    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private OccurrenceService occurrenceService;

    // Mocks para Anexos
    private User mockUser;
    private Occurrence mockOccurrence;

    @BeforeEach
    void setUp() {
        mockUser = UserMock.USER_JOHN_DOE();
        mockUser.setId(1L);

        mockOccurrence = OccurrenceMock.OCCURRENCE_LAMPPOST();
        mockOccurrence.setId(10L);
        mockOccurrence.setReportedBy(mockUser);
    }

    @Test
    @DisplayName("Deve criar uma nova ocorrência")
    void testCreateOccurrence() {
        // Given
        when(userService.findById(1L)).thenReturn(mockUser);
        OccurrenceRequest request = new OccurrenceRequest(OccurrenceMock.OCCURENCE_TITLE,
                OccurrenceMock.OCCURRENCE_DESCRIPTION);

        // Quando o save for chamado, retorne a entidade que foi passada para ele
        when(occurrenceRepository.save(any(Occurrence.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // When
        Occurrence result = occurrenceService.create(request, mockUser.getId());
        // Then
        assertNotNull(result);
        assertEquals(result.getTitle(), OccurrenceMock.OCCURENCE_TITLE);
        assertEquals(result.getStatus(), OccurrenceStatus.OPENED);
        assertEquals(result.getReportedBy(), mockUser);

        verify(userService, times(1)).findById(1L);
        verify(occurrenceRepository, times(1)).save(any(Occurrence.class));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar o status de uma ocorrência fechada")
    void testUpdateStatusClosedOccurrence() {
        // Given
        Occurrence closedOccurrence = OccurrenceMock.OCCURRENCE_LAMPPOST();
        closedOccurrence.setId(1L);
        closedOccurrence.setStatus(OccurrenceStatus.CLOSED);

        when(occurrenceRepository.findById(1L)).thenReturn(Optional.of(closedOccurrence));
        // When & Then
        assertThrows(IllegalStateException.class, () -> occurrenceService.update(1L, OccurrenceStatus.IN_PROGRESS));

        verify(occurrenceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve fazer upload e adicionar anexo quando usuário é o responsável")
    void testAddAttachment() {
        // Given
        when(occurrenceRepository.findById(10L)).thenReturn(Optional.of(mockOccurrence));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "bytes".getBytes());

        // Simula o service de storage
        when(storageService.uploadFile(any(), anyString())).thenReturn("path/to/new-file.jpg");
        // Simula o save no banco
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Attachment result = occurrenceService.addAttachment(mockOccurrence.getId(), file, mockUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(result.getOccurrence(), mockOccurrence);
        assertEquals(result.getFileName(), "test.jpg");
        assertNotEquals(result.getFilePath(), "path/to/test.jpg");

        verify(storageService, times(1)).uploadFile(any(), anyString());
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Deve falhar ao fazer upload e adicionar anexo quando usuário não é o responsável")
    void testAddAttachmentNotAuthorized() {
        // Given
        when(occurrenceRepository.findById(10L)).thenReturn(Optional.of(mockOccurrence));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "bytes".getBytes());

        // When & Then
        assertThrows(AccessDeniedException.class, () -> {
            occurrenceService.addAttachment(10L, file, 2L);
        });

        verify(storageService, never()).uploadFile(any(), anyString());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Deve excluir um anexo quando usuário é o responsável")
    void testDeleteAttachment() {
        // Given
        when(occurrenceRepository.findById(10L)).thenReturn(Optional.of(mockOccurrence));
        Attachment attachment = new Attachment();
        attachment.setId(100L);
        attachment.setOccurrence(mockOccurrence);
        attachment.setFilePath("path/to/delete.jpg");

        when(attachmentRepository.findById(100L)).thenReturn(Optional.of(attachment));
        // Mockar o void delete (não lança exceção)
        doNothing().when(storageService).deleteFile("path/to/delete.jpg");
        doNothing().when(attachmentRepository).delete(attachment);

        // When
        occurrenceService.deleteAttachment(10L, mockUser.getId(), attachment.getId());

        // Then
        verify(storageService, times(1)).deleteFile("path/to/delete.jpg");
        verify(attachmentRepository, times(1)).delete(attachment);
    }

    @Test
    @DisplayName("Deve falhar ao excluir um anexo quando usuário não é o responsável")
    void testDeleteAttachmentNotAuthorized() {
        // When & Then
        when(occurrenceRepository.findById(10L)).thenReturn(Optional.of(mockOccurrence));
        assertThrows(AccessDeniedException.class, () -> {
            occurrenceService.deleteAttachment(10L, 2L, 100L);
        });

        verify(storageService, never()).deleteFile(anyString());
        verify(attachmentRepository, never()).delete(any(Attachment.class));

    }
}
