package br.com.gammonsistemas.asheos.domain.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.gammonsistemas.asheos.core.security.TokenService;
import br.com.gammonsistemas.asheos.domain.auth.dto.LoginRequest;
import br.com.gammonsistemas.asheos.domain.auth.dto.RegisterRequest;
import br.com.gammonsistemas.asheos.domain.auth.dto.TokenResponse;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra um novo usuário
     * 
     * @return Token JWT
     */
    public TokenResponse register(RegisterRequest request) {

        // Verifica se o email já está em uso
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("O email já está em uso");
        }

        User user = new User(
                null,
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                null);

        user = userRepository.save(user);

        // Gera o token JWT
        String token = tokenService.generateToken(user);

        return TokenResponse.builder().token(token).build();
    }

    /**
     * Autentica o usuário
     * 
     * @return Token JWT
     */
    public TokenResponse login(LoginRequest request) {
        // Autentica o usuário
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()));

        UserDetails user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        // Gera o token JWT
        String token = tokenService.generateToken(user);

        return TokenResponse.builder().token(token).build();
    }
}
