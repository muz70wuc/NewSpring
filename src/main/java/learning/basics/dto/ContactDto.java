package learning.basics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactDto {

    @NotBlank(message = "Bitte gib einen Betreff an.")
    @Size(max = 150, message = "Der Betreff darf maximal 150 Zeichen lang sein.")
    private String subject;

    @NotBlank(message = "Die Nachricht darf nicht leer sein.")
    @Size(min = 10, max = 2000, message = "Die Nachricht muss zwischen 10 und 2000 Zeichen lang sein.")
    private String message;

    public ContactDto() {}

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}