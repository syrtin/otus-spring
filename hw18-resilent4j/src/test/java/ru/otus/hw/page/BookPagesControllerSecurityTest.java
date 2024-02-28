package ru.otus.hw.page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.security.SecurityConfiguration;

import java.util.stream.Stream;

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
    @MethodSource("accessTestData")
    void testPageAccessWithRoleAndStatusCode(String role, String url, int expectedStatusCode) throws Exception {
        mockMvc.perform(get(url).with(user("user").roles(role)))
                .andExpect(status().is(expectedStatusCode));
    }

    private static Stream<Arguments> accessTestData() {
        return Stream.of(
                Arguments.of("USER", "/books", 200),
                Arguments.of("USER", "/books/1", 200),
                Arguments.of("ADMIN", "/books/1/edit", 200),
                Arguments.of("ADMIN", "/books/new", 200),
                Arguments.of("USER", "/books/1/edit", 403),
                Arguments.of("USER", "/books/new", 403)
        );
    }
}