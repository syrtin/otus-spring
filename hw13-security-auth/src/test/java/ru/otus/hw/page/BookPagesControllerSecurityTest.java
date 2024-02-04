package ru.otus.hw.page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.security.SecurityConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookPagesController.class)
@Import({SecurityConfiguration.class})
class BookPagesControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWithoutAuthenticationShouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @ParameterizedTest
    @CsvSource({
            "USER, /books",
            "USER, /books/1",
            "ADMIN, /books/1/edit",
            "ADMIN, /books/new"
    })
    void testPageAccessWithRole(String role, String url) throws Exception {
        mockMvc.perform(get(url).with(user("user").roles(role)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "USER, /books/1/edit",
            "USER, /books/new"
    })
    void testUnauthorizedEditFormAccess_ShouldReturnForbidden(String role, String url) throws Exception {
        mockMvc.perform(get(url).with(user("user").roles(role)))
                .andExpect(status().isForbidden());
    }
}