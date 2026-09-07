package learning.basics.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import learning.basics.service.UserService;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "max")
    @DisplayName("GET /contact - Zeigt Kontakt-Formular an")
    void testContactPage() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("registeredUser/contact"))
                .andExpect(model().attributeExists("contactDto"));
    }

    @Test
    @WithMockUser(username = "max")
    @DisplayName("POST /contact - Erfolgreicher Versand")
    void testSendContactMessageSuccess() throws Exception {
        mockMvc.perform(post("/contact")
                        .with(csrf())
                        .param("subject", "Anfrage zu Support")
                        .param("message", "Das ist eine ausreichend lange Testnachricht für den Versand."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).processContactForm(eq("max"), any());
    }

    @Test
    @WithMockUser(username = "max")
    @DisplayName("POST /contact - Gibt Formular mit Validierungsfehlern zurück")
    void testSendContactMessageWithValidationErrors() throws Exception {
        mockMvc.perform(post("/contact")
                        .with(csrf())
                        .param("subject", "")
                        .param("message", "zu kurz"))
                .andExpect(status().isOk())
                .andExpect(view().name("registeredUser/contact"))
                .andExpect(model().hasErrors());

        verify(userService, never()).processContactForm(any(), any());
    }
}