package learning.basics.validationTests;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import learning.basics.dto.ProfileDto;

public class ProfileEmailValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Sollte Fehler werfen bei ungültigem E-Mail Format")
    void testInvalidEmailFormat() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setEmail("invalid-email-format");
        profileDto.setCompanyName("Test Company");

        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(profileDto);
        assertFalse(violations.isEmpty(), "Es sollten Validierungsfehler vorhanden sein.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        , "Es sollte ein Fehler für das E-Mail-Feld vorhanden sein.");
    }

    @Test
    @DisplayName("Sollte Fehler werfen wenn E-Mail leer ist")
    void testBlankEmail() {
        ProfileDto dto = new ProfileDto();
        dto.setEmail("");
        dto.setCompanyName("Test GmbH");

        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
}
