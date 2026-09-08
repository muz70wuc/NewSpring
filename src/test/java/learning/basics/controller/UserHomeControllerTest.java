package learning.basics.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import learning.basics.model.User;
import learning.basics.service.UserService;

@WebMvcTest(UserHomeController.class)
class UserHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "max")
    @DisplayName("GET /userHome - Unvollständiges Profil leitet auf /profile um")
    void testUserHomeIncompleteProfile() throws Exception {
        when(userService.isProfileIncomplete("max")).thenReturn(true);

        mockMvc.perform(get("/userHome"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("infoMessage"));
    }

    @Test
    @WithMockUser(username = "max")
    @DisplayName("GET /userHome - Vollständiges Profil zeigt Dashboard an")
    void testUserHomeCompleteProfile() throws Exception {
        // 1. Mock-Verhalten für isProfileIncomplete festlegen
        when(userService.isProfileIncomplete("max")).thenReturn(false);
    
        // 2. Mock-User erstellen & festlegen, was findByUsername zurückgibt
        User mockUser = new User();
        mockUser.setUsername("max");
        mockUser.setEmail("max@example.com");
        mockUser.setPhoneNumber("+49123456");
        mockUser.setCompanyName("Muster GmbH");
        mockUser.setContactPerson("Max Mustermann");

        when(userService.findByUsername("max")).thenReturn(mockUser);

        // 3. Request ausführen und Ergebnisse prüfen
        mockMvc.perform(get("/userHome"))
                .andExpect(status().isOk())
                .andExpect(view().name("registeredUser/userHome"))
                .andExpect(model().attribute("username", "max"))
                .andExpect(model().attribute("email", "max@example.com"))
                .andExpect(model().attribute("companyName", "Muster GmbH"));
    }
}