package br.com.gammonsistemas.asheos.domain.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityManager entityManager;

    private User mockUser;
    private User mockNewUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(UserMock.USER_NAME, UserMock.USER_EMAIL, UserMock.USER_PASSWORD);
        entityManager.persist(mockUser);

        mockNewUser = new User("New User", "newuser@test.com", "12345678");
        entityManager.persist(mockNewUser);

        Address address = AddressMock.ADDRESS_JOHN_DOE();
        address.setUser(mockUser);
        entityManager.persist(address);
    }

    @Test
    @DisplayName("Deve buscar os endereços do usuário")
    void testFindByUserId() {
        // When
        List<Address> addresses = addressRepository.findByUserId(mockUser.getId());

        // Then
        assertEquals(addresses.size(), 1);
        assertEquals(addresses.get(0).getStreet(), AddressMock.ADDRESS_STREET);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar endereços de usuário sem endereços")
    void testFindByUserIdNotFound() {
        // When
        List<Address> addresses = addressRepository.findByUserId(mockNewUser.getId());

        // Then
        assertEquals(addresses.size(), 0);
    }
}
