package br.com.gammonsistemas.asheos.domain.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.access.AccessDeniedException;

import br.com.gammonsistemas.asheos.domain.address.dto.AddressRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import br.com.gammonsistemas.asheos.domain.user.UserService;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AddressService addressService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = UserMock.USER_JOHN_DOE();
        mockUser.setId(1L);
    }

    @Test
    @DisplayName("Deve criar um novo endereço")
    void testCreateAddress() {
        // Given
        when(userService.findById(1L)).thenReturn(mockUser);

        AddressRequest request = new AddressRequest(
                AddressMock.ADDRESS_STREET,
                AddressMock.ADDRESS_CITY,
                AddressMock.ADDRESS_STATE,
                AddressMock.ADDRESS_ZIP_CODE);

        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHen
        Address result = addressService.create(request, mockUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(result.getStreet(), AddressMock.ADDRESS_STREET);
        assertEquals(result.getUser(), mockUser);

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Deve excluir um endereço")
    void testDeleteAddress() {
        // Given
        Address address = AddressMock.ADDRESS_JOHN_DOE();
        address.setId(10L);
        address.setUser(mockUser);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);

        // When
        addressService.delete(address.getId(), mockUser.getId());

        // Then
        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    @DisplayName("Deve falhar ao excluir um endereço de outro usuário")
    void testDeleteAddressNotAuthorized() {
        // Given
        User mockNewUser = new User();
        mockNewUser.setId(2L);

        Address address = AddressMock.ADDRESS_JOHN_DOE();
        address.setId(10L);
        address.setUser(mockNewUser);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        // When
        assertThrows(AccessDeniedException.class, () -> {
            addressService.delete(address.getId(), 1L);
        });

        verify(addressRepository, never()).delete(any(Address.class));
    }

}
