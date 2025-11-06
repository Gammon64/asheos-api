package br.com.gammonsistemas.asheos.domain.user;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public User findById (Long id) {
        return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("O Usuário não existe!"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("O email de usuário não existe!"));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
