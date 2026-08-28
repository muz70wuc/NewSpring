package learning.basics.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import learning.basics.service.UserService;

@Controller
public class UserHomeController {

    private final UserService userService;

    public UserHomeController(UserService userService) {
        this.userService = userService;
    }

    // Geschützte Startseite nach dem Einloggen
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

        // 2. Normaler Login (Profil ist bereits ausgefüllt)
        model.addAttribute("username", userDetails.getUsername());
        return "registeredUser/userHome";
    }

}