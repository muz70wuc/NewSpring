package learning.basics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import learning.basics.dto.ContactDto;
import learning.basics.model.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Deine eigene Ziel-E-Mail-Adresse aus application.properties
    @Value("${MAIL_TARGET}")
    private String targetEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactEmail(User sender, ContactDto contactDto) {
        SimpleMailMessage mail = new SimpleMailMessage();
        
        mail.setFrom(targetEmail);
        mail.setTo(targetEmail);
        mail.setSubject("[DS-Portfolio] " + contactDto.getSubject());

        // Reply-To setzen, damit du in deinem Mail-Programm direkt auf "Antworten" klicken kannst
        if (sender.getEmail() != null && !sender.getEmail().isBlank()) {
            mail.setReplyTo(sender.getEmail());
        }

        // Automatische Profil-Signatur zusammenbauen
        StringBuilder body = new StringBuilder();
        body.append(contactDto.getMessage()).append("\n\n");
        body.append("-------------------------------------------\n");
        body.append("ABSENDER-PROFIL (Automatisch angehängt):\n");
        body.append("Benutzername: ").append(sender.getUsername()).append("\n");
        body.append("E-Mail:       ").append(sender.getEmail() != null ? sender.getEmail() : "Nicht angegeben").append("\n");
        body.append("Firma:        ").append(sender.getCompanyName() != null ? sender.getCompanyName() : "Nicht angegeben").append("\n");
        body.append("Ansprechp.:   ").append(sender.getContactPerson() != null ? sender.getContactPerson() : "Nicht angegeben").append("\n");
        body.append("Telefon:      ").append(sender.getPhoneNumber() != null ? sender.getPhoneNumber() : "Nicht angegeben").append("\n");
        body.append("-------------------------------------------\n");

        mail.setText(body.toString());

        mailSender.send(mail);
    }
}