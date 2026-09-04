package learning.basics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {
    @GetMapping("/")
    public String index() {
        return "index"; // Öffnet src/main/resources/templates/index.html
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "agreement/privacy"; // Öffnet src/main/resources/templates/agreement/privacy.html
    }
    
    @GetMapping("/terms")
    public String terms() {
        return "agreement/terms"; // Öffnet src/main/resources/templates/agreement/terms.html
    }
    
}
