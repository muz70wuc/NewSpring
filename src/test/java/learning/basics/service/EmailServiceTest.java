package learning.basics.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import learning.basics.dto.ContactDto;
import learning.basics.model.User;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Sollte Kontakt-E-Mail über den MailService verarbeiten ohne echten SMTP-Aufruf")
    void testSendContactEmailSuccess() {
        // Arrange
        User testUser = new User();
        testUser.setUsername("MaxMustermann");
        testUser.setEmail("kunde@beispiel.de");
        testUser.setCompanyName("Musterfirma GmbH");
        testUser.setContactPerson("Max Mustermann");
        testUser.setPhoneNumber("+49 123 456789");

        ContactDto contactDto = new ContactDto();
        contactDto.setSubject("Anfrage für Portfolio-Projekt");
        contactDto.setMessage("Hallo, ich möchte gerne eine Anfrage bezüglich eines B2B-Projekts stellen.");

        // Act & Assert
        assertDoesNotThrow(() -> {
            emailService.sendContactEmail(testUser, contactDto);
        }, "Der E-Mail-Versand sollte ohne Fehler verarbeitet werden");

        // Verify
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}