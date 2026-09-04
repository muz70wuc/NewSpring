package learning.basics.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import learning.basics.annotation.SecurityWebMvcTest;

@SecurityWebMvcTest(controllers = PublicController.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / - Zeigt öffentliche Startseite an")
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("GET / privacy")
    void testPrivacy() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(status().isOk())
                .andExpect(view().name("agreement/privacy"));
    }

    @Test
    @DisplayName("GET / - Zeigt terms für das Registry")
    void testTerms() throws Exception {
        mockMvc.perform(get("/terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("agreement/terms"));
    }
}