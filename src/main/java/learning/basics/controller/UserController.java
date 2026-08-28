package learning.basics.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import learning.basics.dto.ContactDto;
import learning.basics.dto.ProfileDto;
import learning.basics.model.User;
import learning.basics.service.EmailService;
import learning.basics.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
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

    // Profil-Seite anzeigen
    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ProfileDto profileDto = userService.getProfileByUsername(userDetails.getUsername());
        model.addAttribute("profileDto", profileDto);
        return "registeredUser/profile";
    }

    // Profil-Änderungen speichern
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        // Prüfen, ob die E-Mail bereits von jemand anderem genutzt wird
        if (userService.isEmailTakenByAnotherUser(profileDto.getEmail(), userDetails.getUsername())) {
            bindingResult.rejectValue("email", "error.profileDto", "Diese E-Mail-Adresse wird bereits verwendet.");
        }

        // Falls Formate falsch, Felder leer oder E-Mail vergeben -> Zurück zum Formular
        if (bindingResult.hasErrors()) {
            return "registeredUser/profile";
        }

        userService.updateProfile(userDetails.getUsername(), profileDto);
        redirectAttributes.addFlashAttribute("successMessage", "Profil erfolgreich aktualisiert!");
        return "redirect:/profile";
    }

    // Kontakt-Seite anzeigen
    @GetMapping("/contact")
    public String contactPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("contactDto", new ContactDto());
        return "registeredUser/contact";
    }

    // Nachricht verarbeiten & E-Mail auslösen
    @PostMapping("/contact")
    public String sendContactMessage(@Valid @ModelAttribute("contactDto") ContactDto contactDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "registeredUser/contact";
        }

        // Aktuellen User aus der Datenbank laden
        User currentUser = userService.findByUsername(userDetails.getUsername());

        // E-Mail mit Nachricht + angehängten Profildaten versenden
        emailService.sendContactEmail(currentUser, contactDto);

        redirectAttributes.addFlashAttribute("successMessage", "Deine Nachricht wurde erfolgreich übermittelt!");
        return "redirect:/contact";
    }
}