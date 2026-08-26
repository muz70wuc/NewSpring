package learning.basics.controller;

import learning.basics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Zeigt die Login-Seite an
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // Öffnet src/main/resources/templates/login.html
    }

    // Zeigt die Registrierungs-Seite an
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register"; // Öffnet src/main/resources/templates/register.html
    }

    // Verarbeitet das Absenden des Registrierungs-Formulars (POST)
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        userService.registerUser(username, password);
        // Nach erfolgreicher Registrierung leiten wir auf den Login weiter
        return "redirect:/login?registered";
    }
}