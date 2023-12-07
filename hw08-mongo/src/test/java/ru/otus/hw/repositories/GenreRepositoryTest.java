package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataMongoTest
class GenreRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_GENRES_BY_IDS = 2;
    private static final String FIRST_GENRE_ID = "1";
    private static final String SECOND_GENRE_ID = "2";

    @Autowired
    private GenreRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DisplayName("должен загружать список жанров по списку их id")
    @Test
    void shouldReturnCorrectGenresListByIds() {
        var expectedGenre1 = mongoTemplate.findById(FIRST_GENRE_ID, Genre.class);
        var expectedGenre2 = mongoTemplate.findById(SECOND_GENRE_ID, Genre.class);

        var actualGenres = repository.findAllByIdIn(List.of(FIRST_GENRE_ID, SECOND_GENRE_ID));

        assertThat(actualGenres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES_BY_IDS)
                .allMatch(g -> !g.getName().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2);
    }
}