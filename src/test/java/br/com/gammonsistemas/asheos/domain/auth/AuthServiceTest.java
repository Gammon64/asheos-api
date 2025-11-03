package br.com.gammonsistemas.asheos.domain.auth;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.gammonsistemas.asheos.domain.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
}
