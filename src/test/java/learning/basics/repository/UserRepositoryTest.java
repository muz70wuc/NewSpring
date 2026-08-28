package learning.basics.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import learning.basics.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("maxmustermann");
        testUser.setEmail("max@example.com");
        testUser.setPassword("securePassword123!");
    }

    @Test
    @DisplayName("findByUsername - Sollte User zurückgeben, wenn Username existiert")
    void testFindByUsernameSuccess() {
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByUsername("maxmustermann");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("maxmustermann");
        assertThat(foundUser.get().getEmail()).isEqualTo("max@example.com");
    }

    @Test
    @DisplayName("findByUsername - Sollte Optional.empty liefern, wenn Username nicht existiert")
    void testFindByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findByUsername("gibtesnicht");

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("findByEmail - Sollte User zurückgeben, wenn E-Mail existiert")
    void testFindByEmailSuccess() {
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByEmail("max@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("max@example.com");
    }

    @Test
    @DisplayName("findByEmail - Sollte Optional.empty liefern, wenn E-Mail nicht existiert")
    void testFindByEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("unbekannt@example.com");

        assertThat(foundUser).isEmpty();
    }
}