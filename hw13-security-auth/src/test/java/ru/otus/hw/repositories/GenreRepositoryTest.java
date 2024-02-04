package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
class GenreRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_GENRES_BY_IDS = 2;
    private static final long FIRST_GENRE_ID = 1L;
    private static final long SECOND_GENRE_ID = 2L;

    @Autowired
    private GenreRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список жанров по списку их id")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        Genre expectedGenre1 = em.find(Genre.class, FIRST_GENRE_ID);
        Genre expectedGenre2 = em.find(Genre.class, SECOND_GENRE_ID);

        var actualGenres = repository.findAllByIds(List.of(expectedGenre1.getId(), expectedGenre2.getId()));

        assertThat(actualGenres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES_BY_IDS)
                .allMatch(g -> !g.getName().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2);
    }
}