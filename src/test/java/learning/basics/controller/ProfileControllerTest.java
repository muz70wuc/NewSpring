package learning.basics.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

import learning.basics.dto.ProfileDto;
import learning.basics.service.UserService;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "max")
    @DisplayName("GET /profile - Lädt Profile-Page mit DTO")
    void testProfilePage() throws Exception {
        when(userService.getProfileByUsername("max")).thenReturn(new ProfileDto());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("registeredUser/profile"))
                .andExpect(model().attributeExists("profileDto"));
    }

    @Test
    @WithMockUser(username = "max")
    @DisplayName("POST /profile - E-Mail bereits vergeben -> Abbrechen")
    void testUpdateProfileEmailTaken() throws Exception {
        when(userService.isEmailTakenByAnotherUser("existiert@beispiel.de", "max")).thenReturn(true);

        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .param("email", "existiert@beispiel.de")
                        .param("companyName", "Muster GmbH")
                        .param("contactPerson", "Max Mustermann"))
                .andExpect(status().isOk())
                .andExpect(view().name("registeredUser/profile"))
                .andExpect(model().hasErrors());

        verify(userService, never()).updateProfile(eq("max"), any());
    }

    @Test
    @WithMockUser(username = "max")
    @DisplayName("POST /profile - Erfolgreiches Update")
    void testUpdateProfileSuccess() throws Exception {
        when(userService.isEmailTakenByAnotherUser("neu@beispiel.de", "max")).thenReturn(false);

        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .param("email", "neu@beispiel.de")
                        .param("companyName", "Muster GmbH")
                        .param("contactPerson", "Max Mustermann"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).updateProfile(eq("max"), any());
    }
}