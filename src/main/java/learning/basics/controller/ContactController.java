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
import learning.basics.service.UserService;

@Controller
public class ContactController {

    private final UserService userService;

    public ContactController(UserService userService) {
        this.userService = userService;
    }

    // Kontakt-Seite anzeigen
    @GetMapping("/contact")
    public String contactPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("contactDto", new ContactDto());
        return "registeredUser/contact";
    }

    // Nachricht verarbeiten & E-Mail über den UserService auslösen
    @PostMapping("/contact")
    public String sendContactMessage(@Valid @ModelAttribute("contactDto") ContactDto contactDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "registeredUser/contact";
        }

        userService.processContactForm(userDetails.getUsername(), contactDto);

        redirectAttributes.addFlashAttribute("successMessage", "Deine Nachricht wurde erfolgreich übermittelt!");
        return "redirect:/contact";
    }
}