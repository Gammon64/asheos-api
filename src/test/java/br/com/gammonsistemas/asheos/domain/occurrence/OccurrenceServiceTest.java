package br.com.gammonsistemas.asheos.domain.occurrence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private OccurrenceService occurrenceService;

    @Test
    @DisplayName("Deve criar uma nova ocorrência")
    void testCreateOccurrence() {
        // Given
        User mockUser = UserMock.USER_JOHN_DOE();
        mockUser.setId(1L);
        OccurrenceRequest request = new OccurrenceRequest(OccurrenceMock.OCCURENCE_TITLE,
                OccurrenceMock.OCCURRENCE_DESCRIPTION);

        when(userService.findById(1L)).thenReturn(mockUser);
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
        Occurrence closedOccurrence = new Occurrence(
                1L,
                OccurrenceMock.OCCURENCE_TITLE,
                OccurrenceMock.OCCURRENCE_DESCRIPTION,
                OccurrenceStatus.CLOSED,
                null);

        when(occurrenceRepository.findById(1L)).thenReturn(Optional.of(closedOccurrence));
        // When & Then
        assertThrows(IllegalStateException.class, () -> occurrenceService.update(1L, OccurrenceStatus.IN_PROGRESS));

        verify(occurrenceRepository, never()).save(any());
    }
}
