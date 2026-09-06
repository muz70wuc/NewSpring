package learning.basics.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import learning.basics.annotation.SecurityWebMvcTest;
import learning.basics.service.UserService;

@SecurityWebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /login - Sollte Login-View anzeigen")
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /register - Sollte Register-View mit leerem DTO liefern")
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerDto"));
    }

    @Test
    @DisplayName("POST /register - Honeypot befüllt -> Abbrechen ohne Registrierung")
    void testRegisterHoneypot() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "botuser")
                        .param("password", "Password123!")
                        .param("passwordConfirm", "Password123!")
                        .param("website", "http://spam.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("POST /register - Passwort-Fehler -> Zurück zum Formular")
    void testRegisterPasswordMismatch() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "validuser")
                        .param("password", "Password123!")
                        .param("passwordConfirm", "FalschesPW!")
                        .param("website", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("POST /register - Erfolgreiche Registrierung")
    void testRegisterSuccess() throws Exception {
        when(userService.existsUsername("validuser")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "validuser")
                        .param("password", "Password123!")
                        .param("passwordConfirm", "Password123!")
                        .param("termsAccepted", "true")
                        .param("website", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).registerUser(any());
    }
}