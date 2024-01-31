package ru.otus.hw.rest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebFluxTest(BookController.class)
public class BookControllerTest {
    private static final String BOOK_URL = "/api/books";
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
    private WebTestClient webTestClient;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    public void testGetAllBooks() {
        var book1 = getBookWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var book2 = getBookWithId(SECOND_BOOK_ID, SECOND_BOOK_TITLE);
        var bookDto1 = getBookDtoWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var bookDto2 = getBookDtoWithId(SECOND_BOOK_ID, SECOND_BOOK_TITLE);

        when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));
        when(modelMapper.map(book1, BookDto.class)).thenReturn(bookDto1);
        when(modelMapper.map(book2, BookDto.class)).thenReturn(bookDto2);

        webTestClient.get().uri(BOOK_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<BookDto> bookDtoList = response.getResponseBody();
                    assertThat(bookDtoList).contains(bookDto1, bookDto2);
                });
    }

    @Test
    public void testGetBookById() {
        var bookDto = getBookDtoWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var book = getBookWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Mono.just(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        webTestClient.get().uri(BOOK_URL + "/" + FIRST_BOOK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookDto);
    }

    @Test
    public void testEditBook() {
        var book = getBookWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var bookDto = getBookDtoWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Mono.just(book));
        when(bookRepository.save(book)).thenReturn(Mono.just(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        when(authorRepository.findById(FIRST_AUTHOR_ID)).thenReturn(Mono.just(book.getAuthor()));
        when(genreRepository.findAllByIdIn(List.of(FIRST_GENRE_ID, SECOND_GENRE_ID)))
                .thenReturn(Flux.fromIterable(book.getGenres()));

        webTestClient.put().uri(BOOK_URL + "/{id}", FIRST_BOOK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(returnedDto -> {
                    assertEquals(FIRST_BOOK_ID, returnedDto.getId());
                    assertEquals(FIRST_BOOK_TITLE, returnedDto.getTitle());
                    assertEquals(FIRST_AUTHOR_ID, returnedDto.getAuthor().getId());
                    assertEquals(FIRST_GENRE_ID, returnedDto.getGenres().get(0).getId());
                });

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void testNewBook() {
        var book = getBook(FIRST_BOOK_TITLE);
        var bookDto = getBookDtoWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Mono.just(book));
        when(bookRepository.save(book)).thenReturn(Mono.just(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        when(authorRepository.findById(FIRST_AUTHOR_ID)).thenReturn(Mono.just(book.getAuthor()));
        when(genreRepository.findAllByIdIn(List.of(FIRST_GENRE_ID, SECOND_GENRE_ID)))
                .thenReturn(Flux.fromIterable(book.getGenres()));

        webTestClient.post().uri(BOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(returnedDto -> {
                    assertEquals(FIRST_BOOK_ID, returnedDto.getId());
                    assertEquals(FIRST_BOOK_TITLE, returnedDto.getTitle());
                    assertEquals(FIRST_AUTHOR_ID, returnedDto.getAuthor().getId());
                    assertEquals(FIRST_GENRE_ID, returnedDto.getGenres().get(0).getId());
                });
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void testDeleteBook() {
        when(commentRepository.deleteAllByBookId(FIRST_BOOK_ID)).thenReturn(Mono.empty());
        when(bookRepository.deleteById(FIRST_BOOK_ID)).thenReturn(Mono.empty());

        webTestClient.delete().uri(BOOK_URL + "/{id}", FIRST_BOOK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        verify(commentRepository).deleteAllByBookId(FIRST_BOOK_ID);
        verify(bookRepository).deleteById(FIRST_BOOK_ID);
    }

    @Test
    public void testGetAllComments() {
        var bookDto = getBookDtoWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var commentDto1 = new CommentDto(FIRST_COMMENT_ID, FIRST_COMMENT_TEXT, bookDto);
        var commentDto2 = new CommentDto(SECOND_COMMENT_ID, SECOND_COMMENT_TEXT, bookDto);

        var book = getBookWithId(FIRST_BOOK_ID, FIRST_BOOK_TITLE);
        var comment1 = new Comment(FIRST_COMMENT_ID, FIRST_COMMENT_TEXT, book);
        var comment2 = new Comment(SECOND_COMMENT_ID, SECOND_COMMENT_TEXT, book);

        when(commentRepository.findAllByBookId(FIRST_BOOK_ID)).thenReturn(Flux.just(comment1, comment2));
        when(modelMapper.map(comment1, CommentDto.class)).thenReturn(commentDto1);
        when(modelMapper.map(comment2, CommentDto.class)).thenReturn(commentDto2);

        webTestClient.get().uri(BOOK_URL + "/{id}" + COMMENTS, FIRST_BOOK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<CommentDto> commentDtos = response.getResponseBody();
                    assertThat(commentDtos).contains(commentDto1, commentDto2);
                });

        verify(commentRepository).findAllByBookId(FIRST_BOOK_ID);
    }

    private BookDto getBookDtoWithId(String id, String title) {
        var authorDto = new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new BookDto(id, title, authorDto, genres);
    }

    private Book getBookWithId(String id, String title) {
        var author = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new Book(id, title, author, genres);
    }

    private Book getBook(String title) {
        var author = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var genres = List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME));
        return new Book(null, title, author, genres);
    }
}