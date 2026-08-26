package learning.basics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDto {

    @NotBlank(message = "Der Benutzername darf nicht leer sein")
    @Size(min = 3, max = 20, message = "Der Benutzername muss zwischen 3 und 20 Zeichen lang sein")
    private String username;

    @NotBlank(message = "Das Passwort darf nicht leer sein")
    @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen lang sein")
    private String password;

    @NotBlank(message = "Bitte bestätige dein Passwort")
    private String passwordConfirm;

    public RegisterDto() {}

    //Getter und Setter

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }
}
