package learning.basics.validationTests;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import learning.basics.dto.RegisterDto;

class RegisterDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private RegisterDto dto;

    /**
     * Erstellt ein vollständig gültiges RegisterDto als Basis für alle Tests.
     */
    @BeforeEach
    void setUp() {
        dto = new RegisterDto();
        dto.setUsername("testuser");
        dto.setPassword("geheim123");
        dto.setPasswordConfirm("geheim123");
        dto.setTermsAccepted(true);
        dto.setWebsite(""); // Optionales Feld / Honeypot
    }

    // ==========================================
    // 1. GESAMT-VALIDIERUNG
    // ==========================================

    @Test
    @DisplayName("Sollte ein vollständig korrektes RegisterDto ohne Fehler akzeptieren")
    void testValidRegisterDto() {
        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Keine Fehler erwartet bei gültigen Registrierungsdaten");
    }

    // ==========================================
    // 2. BENUTZERNAME VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Username: Sollte Fehler werfen wenn leer, zu kurz (< 3) oder zu lang (> 20)")
    void testUsernameConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setUsername("   ");
        Set<ConstraintViolation<RegisterDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));

        // Test 2: Zu kurz (< 3 Zeichen) (@Size)
        dto.setUsername("ab");
        Set<ConstraintViolation<RegisterDto>> violationsTooShort = validator.validate(dto);
        assertFalse(violationsTooShort.isEmpty());
        assertTrue(violationsTooShort.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));

        // Test 3: Zu lang (> 20 Zeichen) (@Size)
        dto.setUsername("A".repeat(21));
        Set<ConstraintViolation<RegisterDto>> violationsTooLong = validator.validate(dto);
        assertFalse(violationsTooLong.isEmpty());
        assertTrue(violationsTooLong.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    // ==========================================
    // 3. PASSWORT VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Password: Sollte Fehler werfen wenn leer oder zu kurz (< 8)")
    void testPasswordConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setPassword("");
        Set<ConstraintViolation<RegisterDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));

        // Test 2: Zu kurz (< 8 Zeichen) (@Size)
        dto.setPassword("1234567");
        Set<ConstraintViolation<RegisterDto>> violationsTooShort = validator.validate(dto);
        assertFalse(violationsTooShort.isEmpty());
        assertTrue(violationsTooShort.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    // ==========================================
    // 4. PASSWORT-BESTÄTIGUNG VALIDIERUNG (@NotBlank)
    // ==========================================

    @Test
    @DisplayName("PasswordConfirm: Sollte Fehler werfen wenn leer")
    void testPasswordConfirmConstraints() {
        dto.setPasswordConfirm("   ");
        Set<ConstraintViolation<RegisterDto>> violationsBlank = validator.validate(dto);

        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passwordConfirm")));
    }

    // ==========================================
    // 5. NUTZUNGSBEDINGUNG-DATENSCHUTZ-BESTÄTIGUNG VALIDIERUNG (@assertTrue)
    // ==========================================
    @Test
    @DisplayName("termsAccepted: sollte Fehler werfen wenn false")
    void testTermsAcceptedFalse() {
        dto.setTermsAccepted(false);
        Set<ConstraintViolation<RegisterDto>> violationsSetFalse = validator.validate(dto);

        assertFalse(violationsSetFalse.isEmpty());
        assertTrue(violationsSetFalse.stream().anyMatch(v -> v.getPropertyPath().toString().equals("termsAccepted")));
    }
}