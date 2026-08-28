package learning.basics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileDto {

    @NotBlank(message = "Die E-Mail-Adresse darf nicht leer sein.")
    @Email(message = "Bitte gib eine gültige E-Mail-Adresse ein (z. B. name@domain.de).")
    private String email;

    @NotBlank(message = "Der Firmenname darf nicht leer sein.")
    @Size(max = 100, message = "Der Firmenname darf maximal 100 Zeichen lang sein.")
    private String companyName;

    @NotBlank(message = "Der Name der Kontaktperson darf nicht leer sein.")
    @Size(max = 50, message = "Der Name der Kontaktperson darf maximal 50 Zeichen lang sein.")
    private String contactPerson;

    // Erlaubt Ziffern, Leerzeichen, Plus, Bindestriche und Klammern
    @Pattern(
        regexp = "^$|^[+[0-9] \\-()]{6,25}$", 
        message = "Ungültiges Telefonnummer-Format (z. B. +49 123 456789)."
    )
    private String phoneNumber;

    public ProfileDto() {}

    public String getEmail() {
        return email; 
    }

    public void setEmail(String email) {
        this.email = email; 
    }

    public String getCompanyName() {
        return companyName;
    
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    
    }
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
