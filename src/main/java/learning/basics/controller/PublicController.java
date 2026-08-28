package learning.basics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {
    @GetMapping("/")
    public String index() {
        return "index"; // Öffnet src/main/resources/templates/index.html
    }
}
