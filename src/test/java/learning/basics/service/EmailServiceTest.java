package learning.basics.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import learning.basics.dto.ContactDto;
import learning.basics.model.User;

@SpringBootTest
// Liest das Standard-Format aus deiner variable.env direkt im Hauptordner ein:
@TestPropertySource(locations = "file:variable.env")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    @DisplayName("Sendet eine echte Kontakt-E-Mail über 1&1 SMTP")
    void testSendContactEmailSuccess() {
        User testUser = new User();
        testUser.setUsername("MaxMustermann");
        testUser.setEmail("kunde@beispiel.de");
        testUser.setCompanyName("Musterfirma GmbH");
        testUser.setContactPerson("Max Mustermann");
        testUser.setPhoneNumber("+49 123 456789");

        ContactDto contactDto = new ContactDto();
        contactDto.setSubject("Anfrage für Portfolio-Projekt");
        contactDto.setMessage("Hallo, ich möchte gerne eine Anfrage bezüglich eines B2B-Projekts stellen.");

        assertDoesNotThrow(() -> {
            emailService.sendContactEmail(testUser, contactDto);
        }, "Der E-Mail-Versand sollte ohne Fehler durchlaufen");
    }
}