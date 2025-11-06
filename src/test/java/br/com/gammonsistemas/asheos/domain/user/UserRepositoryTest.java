package br.com.gammonsistemas.asheos.domain.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Set up constants for the tests
    @BeforeEach
    void setUp() {
        User user = UserMock.USER_JOHN_DOE();
        userRepository.save(user);
    }

    @Test
    @DisplayName("Deve encontrar usuário pelo e-mail")
    void testFindByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail(UserMock.USER_EMAIL);
        // Then
        assertTrue(foundUser.isPresent());
        assertTrue(foundUser.get().getEmail().equals(UserMock.USER_EMAIL));
    }

    @Test
    @DisplayName("Deve retornar vazio ao tentar encontrar usuário pelo e-mail inválido")
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("notfound@test.com");
        // Then
        assertTrue(foundUser.isEmpty());
    }
}
