package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
@Import(GenreRepositoryJPA.class)
class GenreRepositoryJPATest {

    private static final int EXPECTED_NUMBER_OF_GENRES_BY_IDS = 2;
    private static final int EXPECTED_NUMBER_OF_GENRES_ALL = 6;
    private static final long FIRST_GENRE_ID = 1L;
    private static final long SECOND_GENRE_ID = 2L;
    private static final int EXPECTED_QUERIES_COUNT_BY_IDS = 3;
    private static final int EXPECTED_QUERIES_COUNT_ALL = 1;

    @Autowired
    private GenreRepositoryJPA repositoryJPA;

    @Autowired
    private TestEntityManager em;

    @AfterEach
    void resetStatistics() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().clear();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        var actualGenres = repositoryJPA.findAll();

        assertThat(actualGenres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES_ALL)
                .allMatch(g -> !g.getName().equals(""));

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_ALL);
    }

    @DisplayName("должен загружать список жанров по списку их id")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        Genre expectedGenre1 = em.find(Genre.class, FIRST_GENRE_ID);
        Genre expectedGenre2 = em.find(Genre.class, SECOND_GENRE_ID);


        var actualGenres = repositoryJPA.findAllByIds(List.of(expectedGenre1.getId(), expectedGenre2.getId()));

        assertThat(actualGenres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES_BY_IDS)
                .allMatch(g -> !g.getName().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2);

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_BY_IDS);
    }
}