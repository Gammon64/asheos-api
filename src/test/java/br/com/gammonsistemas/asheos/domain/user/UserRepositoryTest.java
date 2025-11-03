package br.com.gammonsistemas.asheos.domain.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    // Set up constants for the tests
    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@test.com");
        user.setPassword("strongPassword123");
        userRepository.save(user);
    }

    @Test // Try to find user by email
    void testFindByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("johndoe@test.com");
        // Then
        assertTrue(foundUser.isPresent());
        assertTrue(foundUser.get().getEmail().equals("johndoe@test.com"));
    }

    @Test // Try to find user by email that does not exist
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("notfound@test.com");
        // Then
        assertTrue(foundUser.isEmpty());
    }
}
