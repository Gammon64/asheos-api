package br.com.gammonsistemas.asheos.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.gammonsistemas.asheos.core.security.TokenService;
import br.com.gammonsistemas.asheos.domain.auth.dto.RegisterRequest;
import br.com.gammonsistemas.asheos.domain.auth.dto.TokenResponse;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import br.com.gammonsistemas.asheos.domain.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve registrar com dados válidos")
    void testRegisterWithValidData() {
        // Given
        RegisterRequest request = new RegisterRequest(UserMock.USER_NAME, UserMock.USER_EMAIL,
                UserMock.USER_PASSWORD);

        User usuarioSalvo = UserMock.USER_JOHN_DOE();
        String mockJwtToken = "mock.jwt.token";

        when(passwordEncoder.encode(UserMock.USER_PASSWORD)).thenReturn("HashedPAssword");
        when(userRepository.save(any(User.class))).thenReturn(usuarioSalvo);
        when(tokenService.generateToken(usuarioSalvo)).thenReturn(mockJwtToken);
        // When
        TokenResponse response = authService.register(request);
        // Then
        assertNotNull(response);
        assertEquals(response.token(), mockJwtToken);

        verify(passwordEncoder, times(1)).encode(UserMock.USER_PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, times(1)).generateToken(usuarioSalvo);
    }
}
