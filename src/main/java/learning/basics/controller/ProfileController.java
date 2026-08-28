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
import learning.basics.dto.ProfileDto;
import learning.basics.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
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
}
