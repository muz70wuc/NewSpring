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
import learning.basics.dto.ProfileDto;

class ProfileDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private ProfileDto dto;

    /**
     * Erstellt ein vollständig gültiges ProfileDto als Basis für alle Tests.
     */
    @BeforeEach
    void setUp() {
        dto = new ProfileDto();
        dto.setEmail("max.mustermann@beispiel.de");
        dto.setCompanyName("Muster GmbH");
        dto.setContactPerson("Max Mustermann");
        dto.setPhoneNumber("+49 123 456789");
    }

    // ==========================================
    // 1. GESAMT-VALIDIERUNG
    // ==========================================

    @Test
    @DisplayName("Sollte ein vollständig korrektes ProfileDto ohne Fehler akzeptieren")
    void testValidProfile() {
        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Keine Fehler erwartet bei gültigen Daten");
    }

    // ==========================================
    // 2. E-MAIL VALIDIERUNG (@NotBlank, @Email)
    // ==========================================

    @Test
    @DisplayName("E-Mail: Sollte Fehler werfen wenn leer, blank oder ungültiges Format")
    void testEmailConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setEmail("   ");
        Set<ConstraintViolation<ProfileDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        // Test 2: Falsches Format (@Email)
        dto.setEmail("falsche-email-ohne-at");
        Set<ConstraintViolation<ProfileDto>> violationsInvalid = validator.validate(dto);
        assertFalse(violationsInvalid.isEmpty());
        assertTrue(violationsInvalid.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    // ==========================================
    // 3. FIRMENNAME VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Firmenname: Sollte Fehler werfen wenn leer oder länger als 100 Zeichen")
    void testCompanyNameConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setCompanyName("");
        Set<ConstraintViolation<ProfileDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("companyName")));

        // Test 2: Zu lang (> 100 Zeichen) (@Size)
        dto.setCompanyName("A".repeat(101));
        Set<ConstraintViolation<ProfileDto>> violationsTooLong = validator.validate(dto);
        assertFalse(violationsTooLong.isEmpty());
        assertTrue(violationsTooLong.stream().anyMatch(v -> v.getPropertyPath().toString().equals("companyName")));
    }

    // ==========================================
    // 4. KONTAKTPERSON VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Kontaktperson: Sollte Fehler werfen wenn leer oder länger als 50 Zeichen")
    void testContactPersonConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setContactPerson("  ");
        Set<ConstraintViolation<ProfileDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactPerson")));

        // Test 2: Zu lang (> 50 Zeichen) (@Size)
        dto.setContactPerson("A".repeat(51));
        Set<ConstraintViolation<ProfileDto>> violationsTooLong = validator.validate(dto);
        assertFalse(violationsTooLong.isEmpty());
        assertTrue(violationsTooLong.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactPerson")));
    }

    // ==========================================
    // 5. TELEFONNUMMER VALIDIERUNG (@Pattern)
    // ==========================================

    @Test
    @DisplayName("Telefonnummer: Sollte leeren String akzeptieren, aber bei falschem Format fehlschlagen")
    void testPhoneNumberConstraints() {
        // Test 1: Leerer String ist erlaubt laut RegEx (^$)
        dto.setPhoneNumber("");
        Set<ConstraintViolation<ProfileDto>> violationsEmpty = validator.validate(dto);
        assertTrue(violationsEmpty.isEmpty(), "Leere Telefonnummer sollte erlaubt sein");

        // Test 2: Ungültiges Format (Buchstaben / Sonderzeichen)
        dto.setPhoneNumber("abc-123-invalid!");
        Set<ConstraintViolation<ProfileDto>> violationsInvalid = validator.validate(dto);
        assertFalse(violationsInvalid.isEmpty());
        assertTrue(violationsInvalid.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));

        // Test 3: Zu kurz (< 6 Zeichen)
        dto.setPhoneNumber("+123");
        Set<ConstraintViolation<ProfileDto>> violationsTooShort = validator.validate(dto);
        assertFalse(violationsTooShort.isEmpty());
        assertTrue(violationsTooShort.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
    }
}