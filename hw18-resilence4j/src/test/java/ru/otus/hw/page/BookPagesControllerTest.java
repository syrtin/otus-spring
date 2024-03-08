package ru.otus.hw.page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BookPagesController.class)
class BookPagesControllerTest {
    private static final String BOOK_URL = "/books";
    private static final String FIRST_BOOK_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddListPageShouldReturnBookListPage() throws Exception {
        mockMvc.perform(get(BOOK_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"));
    }

    @Test
    void testShowBookDetailsShouldReturnBookDetailsPage() throws Exception {
        mockMvc.perform(get(BOOK_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attribute("bookId", FIRST_BOOK_ID));
    }

    @Test
    void testShowEditFormShouldReturnBookEditPage() throws Exception {
        mockMvc.perform(get(BOOK_URL + "/{id}/edit", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpect(model().attribute("bookId", FIRST_BOOK_ID));
    }

    @Test
    void testShowNewFormShouldReturnBookNewPage() throws Exception {
        mockMvc.perform(get(BOOK_URL + "/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-new"));
    }
}