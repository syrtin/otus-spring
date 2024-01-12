package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    private static final String BOOK_URL = "/api/books";
    private static final String AUTHORS_URL = "/api/authors";
    private static final String GENRES_URL = "/api/genres";
    private static final String COMMENTS = "/comments";
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

    @Autowired
    private ObjectMapper mapper;

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

        mockMvc.perform(get(BOOK_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)));
    }

    @Test
    public void testGetBookById() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        var comment1 = new CommentDto(FIRST_COMMENT_ID, FIRST_COMMENT_TEXT, bookDto);
        var comment2 = new CommentDto(SECOND_COMMENT_ID, SECOND_COMMENT_TEXT, bookDto);
        var comments = List.of(comment1, comment2);

        given(commentService.getByBookId(FIRST_BOOK_ID)).willReturn(comments);
        given(bookService.findById(FIRST_BOOK_ID)).willReturn(bookDto);

        mockMvc.perform(get(BOOK_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));
    }

    @Test
    public void testEditBook() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        when(bookService.update(anyString(), anyString(), anyString(), anyList())).thenReturn(bookDto);

        mockMvc.perform(put(BOOK_URL + "/{id}", FIRST_BOOK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));

        verify(bookService, times(1)).update(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testNewBook() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_TITLE);

        when(bookService.insert(anyString(), anyString(), anyList())).thenReturn(bookDto);

        mockMvc.perform(post(BOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));

        verify(bookService, times(1)).insert(anyString(), anyString(), anyList());
    }

    @Test
    public void testDeleteBook() throws Exception {
        mockMvc.perform(delete(BOOK_URL + "/{id}", FIRST_BOOK_ID))
                .andExpect(status().isOk());
        verify(bookService).deleteById(FIRST_BOOK_ID);
    }

    @Test
    public void testGetAllAuthors() throws Exception {
        var authors = List.of(new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME));

        given(authorService.findAll()).willReturn(authors);

        mockMvc.perform(get(AUTHORS_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authors)));
    }

    @Test
    public void testGetAllGenres() throws Exception {
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME));

        given(genreService.findAll()).willReturn(genres);

        mockMvc.perform(get(GENRES_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(genres)));
    }

    @Test
    public void testGetAllComments() throws Exception {
        var bookDto = getBookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        var comment1 = new CommentDto(FIRST_COMMENT_ID, FIRST_COMMENT_TEXT, bookDto);
        var comment2 = new CommentDto(SECOND_COMMENT_ID, SECOND_COMMENT_TEXT, bookDto);
        var comments = List.of(comment1, comment2);

        given(commentService.getByBookId(FIRST_BOOK_ID)).willReturn(comments);

        mockMvc.perform(get(BOOK_URL + "/{id}" + COMMENTS, FIRST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comments)));
    }

    private BookDto getBookDto(String id, String title) {
        var authorDto = new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new BookDto(id, title, authorDto, genres);
    }

    private BookDto getBookDto(String title) {
        var authorDto = new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new BookDto(null, title, authorDto, genres);
    }
}