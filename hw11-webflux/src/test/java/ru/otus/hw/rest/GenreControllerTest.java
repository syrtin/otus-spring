package ru.otus.hw.rest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(GenreController.class)
class GenreControllerTest {
    private static final String GENRES_URL = "/api/genres";
    public static final String FIRST_GENRE_ID = ObjectId.get().toString();
    public static final String FIRST_GENRE_NAME = "Genre_1";
    public static final String SECOND_GENRE_ID = ObjectId.get().toString();
    public static final String SECOND_GENRE_NAME = "Genre_2";

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetAllGenres() {
        var genre1 = new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME);
        var genre2 = new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME);
        var genres = List.of(genre1, genre2);

        when(genreRepository.findAll()).thenReturn(Flux.fromIterable(genres));

        webTestClient.get().uri(GENRES_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Genre.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<Genre> genreDtos = response.getResponseBody();
                    assertThat(genreDtos).contains(genre1, genre2);
                });

        verify(genreRepository).findAll();
    }
}