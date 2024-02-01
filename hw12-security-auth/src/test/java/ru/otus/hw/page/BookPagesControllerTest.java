package ru.otus.hw.page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookPagesController.class)
@Import({SecurityConfiguration.class})
class BookPagesControllerTest {
    private static final String FIRST_BOOK_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "USER")
    void testAddListPage_WithUserRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowBookDetails_WithUserRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(get("/books/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attribute("bookId", FIRST_BOOK_ID));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowEditForm_WithAdminRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(get("/books/{id}/edit", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpect(model().attribute("bookId", FIRST_BOOK_ID));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowNewForm_WithAdminRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-new"));
    }

    @Test
    void testAddListPage_WithoutAuthentication_ShouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowEditForm_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/books/{id}/edit", FIRST_BOOK_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowNewForm_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isForbidden());
    }
}
