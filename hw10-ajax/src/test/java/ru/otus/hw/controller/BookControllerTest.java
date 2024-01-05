package ru.otus.hw.controller;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(AuthorDtoEditor.class)
public class BookControllerTest {
    private static final String BASE_URL = "/books";
    public static final String FIRST_BOOK_ID = ObjectId.get().toString();
    public static final String SECOND_BOOK_ID = ObjectId.get().toString();
    public static final String FIRST_BOOK_TITLE = "Book_1";
    public static final String SECOND_BOOK_TITLE = "Book_2";
    public static final String FIRST_AUTHOR_ID = ObjectId.get().toString();
    public static final String FIRST_AUTHOR_FULLNAME = "Author_1";
    public static final String FIRST_COMMENT_ID = ObjectId.get().toString();
    public static final String FIRST_COMMENT_TEXT = "Text_1";
    public static final String SECOND_COMMENT_ID = ObjectId.get().toString();
    public static final String SECOND_COMMENT_TEXT = "Text_2";
    public static final String FIRST_GENRE_ID = ObjectId.get().toString();
    public static final String FIRST_GENRE_NAME = "Genre_1";
    public static final String SECOND_GENRE_ID = ObjectId.get().toString();
    public static final String SECOND_GENRE_NAME = "Genre 2";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @Test
    public void testGetAllBooks() throws Exception {
        var bookDto1 = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var bookDto2 = getBookDto(SECOND_BOOK_ID, SECOND_BOOK_TITLE);
        var books = List.of(bookDto1, bookDto2);

        given(bookService.findAll()).willReturn(books);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"))
                .andExpect(model().attribute("books", hasSize(2)));
    }

    @Test
    public void testGetBookById() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        var comment1 = new CommentDto(FIRST_COMMENT_ID, FIRST_COMMENT_TEXT, bookDto);
        var comment2 = new CommentDto(SECOND_COMMENT_ID, SECOND_COMMENT_TEXT, bookDto);
        var comments = List.of(comment1, comment2);

        given(commentService.getByBookId(FIRST_BOOK_ID)).willReturn(comments);
        given(bookService.findById(FIRST_BOOK_ID)).willReturn(Optional.of(bookDto));

        mockMvc.perform(get(BASE_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attribute("book", hasProperty("id", is(FIRST_BOOK_ID))))
                .andExpect(model().attribute("book", hasProperty("title", is(FIRST_BOOK_TITLE))))
                .andExpect(model().attribute("book", hasProperty("author",
                        hasProperty("id", is(FIRST_AUTHOR_ID)))))
                .andExpect(model().attribute("book", hasProperty("genres", hasSize(2))))
                .andExpect(model().attribute("comments", hasSize(2)));
    }

    @Test
    public void testEditBook() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        when(bookService.update(anyString(), anyString(), anyString(), anyList())).thenReturn(bookDto);

        mockMvc.perform(post(BASE_URL + "/{id}" + "/edit", FIRST_BOOK_ID)
                        .flashAttr("book", bookDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).update(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testGetBookById_notFound() throws Exception {
        given(bookService.findById(FIRST_BOOK_ID)).willReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("not-found"));
    }

    @Test
    public void testShowEditFormWithEmptyId() throws Exception {
        var authors = List.of(new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME));
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME));

        given(authorService.findAll()).willReturn(authors);
        given(genreService.findAll()).willReturn(genres);

        mockMvc.perform(get(BASE_URL + "/{id}/edit", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testShowEditFormWithExistingId() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        given(bookService.findById(FIRST_BOOK_ID)).willReturn(Optional.of(bookDto));
        given(authorService.findAll()).willReturn(new ArrayList<>());
        given(genreService.findAll()).willReturn(new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/edit", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", hasProperty("id", is(FIRST_BOOK_ID))))
                .andExpect(model().attribute("book", hasProperty("title", is(FIRST_BOOK_TITLE))))
                .andExpect(model().attribute("book", hasProperty("author",
                        hasProperty("id", is(FIRST_AUTHOR_ID)))))
                .andExpect(model().attribute("book", hasProperty("genres", hasSize(2))));
    }

    @Test
    public void testDeleteBook() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().is3xxRedirection());
        verify(bookService).deleteById(FIRST_BOOK_ID);
    }

    private BookDto getBookDto(String id, String title) {
        var authorDto = new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new BookDto(id, title, authorDto, genres);
    }
}