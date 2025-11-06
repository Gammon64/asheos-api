package br.com.gammonsistemas.asheos.domain.occurrence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class OccurrenceRepositoryTest {

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @Autowired
    EntityManager entityManager;

    private User user;
    private Occurrence occurrence;

    // Set up constants for the tests
    @BeforeEach
    void setUp() {
        user = UserMock.USER_JOHN_DOE();
        entityManager.persist(user);
        occurrence = new Occurrence(
                null,
                OccurrenceMock.OCCURENCE_TITLE,
                OccurrenceMock.OCCURRENCE_DESCRIPTION,
                OccurrenceStatus.OPENED, user);
        occurrenceRepository.save(occurrence);
    }

    @Test
    @DisplayName("Deve encontrar ocorrência pelo responsável")
    void testFindByEmail() {
        // When
        List<Occurrence> foundOccurrences = occurrenceRepository
                .findByReportedBy_id(occurrence.getReportedBy().getId());
        // Then
        assertNotNull(foundOccurrences);
        assertEquals(foundOccurrences.size(), 1);
        assertTrue(foundOccurrences.get(0).getTitle().equals(occurrence.getTitle()));
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar ocorrências de usuário sem ocorrências")
    void testFindByEmailNotFound() {
        // Given
        User newUser = new User(
                null,
                "New User",
                "newuser@test.com",
                "12345678");
        entityManager.persist(newUser);
        // When
        List<Occurrence> foundOccurrences = occurrenceRepository.findByReportedBy_id(newUser.getId());
        // Then
        assertTrue(foundOccurrences.isEmpty());
    }
}
