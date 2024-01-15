package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {
    private static final String AUTHORS_URL = "/api/authors";
    public static final String FIRST_AUTHOR_ID = ObjectId.get().toString();
    public static final String FIRST_AUTHOR_FULLNAME = "Author_1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthorService authorService;

    @Test
    public void testGetAllAuthors() throws Exception {
        var authors = List.of(new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME));

        given(authorService.findAll()).willReturn(authors);

        mockMvc.perform(get(AUTHORS_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authors)));
    }
}