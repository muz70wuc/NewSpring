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
import learning.basics.dto.ContactDto;

class ContactDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private ContactDto dto;

    /**
        * Erstellt ein vollständig gültiges ContactDto als Basis für alle Tests.
        */
    @BeforeEach
    void setUp() {
        dto = new ContactDto();
        dto.setSubject("Anfrage zu Support");
        dto.setMessage("Hallo Team, ich habe eine Frage bezüglich des Profils.");
    }


    // ==========================================
    // 1. GESAMT-VALIDIERUNG
    // ==========================================

    @Test
    @DisplayName("Sollte ein vollständig korrektes ContactDto ohne Fehler akzeptieren")
    void testValidContactDto() {
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Keine Fehler erwartet bei gültigen Kontaktdaten");
    }

    // ==========================================
    // 2. BETREFF VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Betreff: Sollte Fehler werfen wenn leer oder länger als 150 Zeichen")
    void testSubjectConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setSubject("   ");
        Set<ConstraintViolation<ContactDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subject")));

        // Test 2: Zu lang (> 150 Zeichen) (@Size)
        dto.setSubject("A".repeat(151));
        Set<ConstraintViolation<ContactDto>> violationsTooLong = validator.validate(dto);
        assertFalse(violationsTooLong.isEmpty());
        assertTrue(violationsTooLong.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subject")));
    }

    // ==========================================
    // 3. NACHRICHT VALIDIERUNG (@NotBlank, @Size)
    // ==========================================

    @Test
    @DisplayName("Nachricht: Sollte Fehler werfen wenn leer, zu kurz (< 10) oder zu lang (> 2000)")
    void testMessageConstraints() {
        // Test 1: Leer / Blank (@NotBlank)
        dto.setMessage("");
        Set<ConstraintViolation<ContactDto>> violationsBlank = validator.validate(dto);
        assertFalse(violationsBlank.isEmpty());
        assertTrue(violationsBlank.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")));

        // Test 2: Zu kurz (< 10 Zeichen) (@Size)
        dto.setMessage("123456789"); // 9 Zeichen
        Set<ConstraintViolation<ContactDto>> violationsTooShort = validator.validate(dto);
        assertFalse(violationsTooShort.isEmpty());
        assertTrue(violationsTooShort.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")));

        // Test 3: Zu lang (> 2000 Zeichen) (@Size)
        dto.setMessage("A".repeat(2001));
        Set<ConstraintViolation<ContactDto>> violationsTooLong = validator.validate(dto);
        assertFalse(violationsTooLong.isEmpty());
        assertTrue(violationsTooLong.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")));
    }
}