package learning.basics.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import learning.basics.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring baut die SQL-Abfrage automatisch:
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
