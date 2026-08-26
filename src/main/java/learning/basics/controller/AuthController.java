package learning.basics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import learning.basics.dto.RegisterDto;
import learning.basics.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Zeigt die Login-Seite an
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // Öffnet src/main/resources/templates/login.html
    }

    // Zeigt die Registrierungs-Seite an
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register"; // Öffnet src/main/resources/templates/register.html
    }

    // Verarbeitet das Absenden des Registrierungs-Formulars (POST)
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult bindingResult,
                               Model model) {
        // 1. Manuelle Logik-Prüfungen
        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.registerDto", "Die Passwörter stimmen nicht überein.");
        }

        if (userService.existsUsername(registerDto.getUsername())) {
            bindingResult.rejectValue("username", "error.registerDto", "Dieser Benutzername ist bereits vergeben.");
        }

        // 2. Falls Validierungsfehler vorliegen -> zurück zum Formular!
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // 3. Erfolgreich registrieren
        userService.registerUser(registerDto);
        return "redirect:/login?registered";
    }
}