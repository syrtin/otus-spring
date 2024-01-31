package ru.otus.hw.rest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {
    private static final String AUTHORS_URL = "/api/authors";
    public static final String FIRST_AUTHOR_ID = ObjectId.get().toString();
    public static final String FIRST_AUTHOR_FULLNAME = "Author_1";
    public static final String SECOND_AUTHOR_ID = ObjectId.get().toString();
    public static final String SECOND_AUTHOR_FULLNAME = "Author_2";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private AuthorRepository authorRepository;

    @Test
    public void testGetAllAuthors(){
        var author1 = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var author2 = new Author(SECOND_AUTHOR_ID, SECOND_AUTHOR_FULLNAME);
        var authors = List.of(author1, author2);
        var authorDto1 = new AuthorDto(FIRST_AUTHOR_ID, FIRST_AUTHOR_FULLNAME);
        var authorDto2 = new AuthorDto(SECOND_AUTHOR_ID, SECOND_AUTHOR_FULLNAME);

        when(authorRepository.findAll()).thenReturn(Flux.fromIterable(authors));
        when(modelMapper.map(author1, AuthorDto.class)).thenReturn(authorDto1);
        when(modelMapper.map(author2, AuthorDto.class)).thenReturn(authorDto2);

        webTestClient.get().uri(AUTHORS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<AuthorDto> authorDtos = response.getResponseBody();
                    assertThat(authorDtos).contains(authorDto1, authorDto2);
                });

        verify(authorRepository).findAll();
    }
}