package learning.basics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import learning.basics.model.User;
import learning.basics.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("max");
        testUser.setPassword("encodedPassword123");
    }

    @Test
    @DisplayName("LoadUserByUsername: Sollte UserDetails zurückgeben, wenn User existiert")
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("max");

        assertNotNull(userDetails);
        assertEquals("max", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());

        verify(userRepository).findByUsername("max");
    }

    @Test
    @DisplayName("LoadUserByUsername: Sollte UsernameNotFoundException werfen, wenn User nicht existiert")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("unbekannt")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unbekannt");
        });

        verify(userRepository).findByUsername("unbekannt");
    }
}