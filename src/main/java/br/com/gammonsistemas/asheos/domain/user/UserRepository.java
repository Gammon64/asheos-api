package br.com.gammonsistemas.asheos.domain.user;

import java.util.Optional;
<<<<<<< HEAD
import java.util.UUID;
=======
>>>>>>> fd75fc75e51f2cbac4cdc9acd8ed14239caa4a68

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
<<<<<<< HEAD
public interface UserRepository extends JpaRepository<User, UUID> {
=======
public interface UserRepository extends JpaRepository<User, String> {
>>>>>>> fd75fc75e51f2cbac4cdc9acd8ed14239caa4a68
    Optional<User> findByEmail(String email);
}
