package learning.basics.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import learning.basics.model.User; // Passe den Import an deine Entity an
import learning.basics.service.UserService;

@Controller
public class UserHomeController {

    private final UserService userService;

    public UserHomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userHome")
    public String userHomePage(@AuthenticationPrincipal UserDetails userDetails, 
                                Model model, 
                                RedirectAttributes redirectAttributes) {

        // 1. Erstes Mal nach Erstellung? -> Umleitung zum Profil
        if (userService.isProfileIncomplete(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("infoMessage", 
                "Willkommen! Bitte vervollständige zuerst deine Profil- und Kontaktdaten.");
            return "redirect:/profile";
        }

        // 2. Benutzer-Entity aus der Datenbank abrufen
        User user = userService.findByUsername(userDetails.getUsername());

        // 3. Alle relevanten Daten an das Template übergeben
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phoneNumber", user.getPhoneNumber());
        model.addAttribute("companyName", user.getCompanyName());
        model.addAttribute("contactPerson", user.getContactPerson());

        return "registeredUser/userHome";
    }
}