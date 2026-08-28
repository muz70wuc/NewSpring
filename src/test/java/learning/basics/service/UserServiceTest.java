package learning.basics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import learning.basics.dto.ProfileDto;
import learning.basics.dto.RegisterDto;
import learning.basics.mapper.UserMapper;
import learning.basics.model.User;
import learning.basics.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private ProfileDto testProfileDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("max");
        testUser.setEmail("max@beispiel.de");
        testUser.setCompanyName("Muster GmbH");
        testUser.setPassword("rawPassword");

        testProfileDto = new ProfileDto();
        testProfileDto.setEmail("neu@beispiel.de");
        testProfileDto.setPhoneNumber("+49 12345");
        testProfileDto.setCompanyName("Neue Firma");
        testProfileDto.setContactPerson("Max Mustermann");
    }

    // --- existsUsername ---
    @Test
    @DisplayName("existsUsername: Sollte true zurückgeben, wenn Username existiert")
    void testExistsUsernameTrue() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        assertTrue(userService.existsUsername("max"));
    }

    @Test
    @DisplayName("existsUsername: Sollte false zurückgeben, wenn Username nicht existiert")
    void testExistsUsernameFalse() {
        when(userRepository.findByUsername("unbekannt")).thenReturn(Optional.empty());
        assertFalse(userService.existsUsername("unbekannt"));
    }

    // --- findByUsername ---
    @Test
    @DisplayName("findByUsername: Sollte User zurückgeben")
    void testFindByUsernameSuccess() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        User result = userService.findByUsername("max");
        assertEquals("max", result.getUsername());
    }

    @Test
    @DisplayName("findByUsername: Sollte UsernameNotFoundException werfen")
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("unbekannt")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("unbekannt"));
    }

    // --- registerUser ---
    @Test
    @DisplayName("registerUser: Sollte Passwort verschlüsseln, mappen und speichern")
    void testRegisterUserSuccess() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("max");
        registerDto.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerDto, "encodedPassword")).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User registered = userService.registerUser(registerDto);

        assertNotNull(registered);
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(testUser);
    }

    // --- getProfileByUsername ---
    @Test
    @DisplayName("getProfileByUsername: Sollte ProfileDto zurückgeben")
    void testGetProfileByUsernameSuccess() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        when(userMapper.toProfileDto(testUser)).thenReturn(testProfileDto);

        ProfileDto result = userService.getProfileByUsername("max");

        assertNotNull(result);
        assertEquals("neu@beispiel.de", result.getEmail());
    }

    // --- updateProfile ---
    @Test
    @DisplayName("updateProfile: Sollte User-Felder aktualisieren und speichern")
    void testUpdateProfileSuccess() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));

        userService.updateProfile("max", testProfileDto);

        assertEquals("neu@beispiel.de", testUser.getEmail());
        assertEquals("+49 12345", testUser.getPhoneNumber());
        assertEquals("Neue Firma", testUser.getCompanyName());
        assertEquals("Max Mustermann", testUser.getContactPerson());
        verify(userRepository).save(testUser);
    }

    // --- isProfileIncomplete ---
    @Test
    @DisplayName("isProfileIncomplete: Sollte false zurückgeben, wenn Email und Company da sind")
    void testIsProfileIncompleteFalse() {
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        assertFalse(userService.isProfileIncomplete("max"));
    }

    @Test
    @DisplayName("isProfileIncomplete: Sollte true zurückgeben, wenn Email fehlt")
    void testIsProfileIncompleteTrueMissingEmail() {
        testUser.setEmail(null);
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        assertTrue(userService.isProfileIncomplete("max"));
    }

    @Test
    @DisplayName("isProfileIncomplete: Sollte true zurückgeben, wenn Company Name leer ist")
    void testIsProfileIncompleteTrueBlankCompany() {
        testUser.setCompanyName("   ");
        when(userRepository.findByUsername("max")).thenReturn(Optional.of(testUser));
        assertTrue(userService.isProfileIncomplete("max"));
    }

    // --- isEmailTakenByAnotherUser ---
    @Test
    @DisplayName("isEmailTakenByAnotherUser: Sollte false bei null oder leerer E-Mail liefern")
    void testIsEmailTakenNullOrBlank() {
        assertFalse(userService.isEmailTakenByAnotherUser(null, "max"));
        assertFalse(userService.isEmailTakenByAnotherUser("   ", "max"));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("isEmailTakenByAnotherUser: Sollte true liefern, wenn E-Mail einem ANDEREN User gehört")
    void testIsEmailTakenByOtherUser() {
        User otherUser = new User();
        otherUser.setUsername("anna");

        when(userRepository.findByEmail("test@beispiel.de")).thenReturn(Optional.of(otherUser));

        assertTrue(userService.isEmailTakenByAnotherUser("test@beispiel.de", "max"));
    }

    @Test
    @DisplayName("isEmailTakenByAnotherUser: Sollte false liefern, wenn E-Mail dem AKTUELLEN User gehört")
    void testIsEmailTakenBySameUser() {
        when(userRepository.findByEmail("max@beispiel.de")).thenReturn(Optional.of(testUser));

        assertFalse(userService.isEmailTakenByAnotherUser("max@beispiel.de", "max"));
    }
}