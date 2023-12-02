package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest
@Import(AuthorRepositoryJPA.class)
class AuthorRepositoryJPATest {
    private static final Long FIRST_AUTHOR_ID = 1L;
    private static final Long SECOND_AUTHOR_ID = 2L;
    private static final Long THIRD_AUTHOR_ID = 3L;
    private static final int EXPECTED_NUMBER_OF_AUTHORS = 3;

    @Autowired
    private AuthorRepositoryJPA repositoryJPA;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var expectedAuthor1 = em.find(Author.class, FIRST_AUTHOR_ID);
        var expectedAuthor2 = em.find(Author.class, SECOND_AUTHOR_ID);
        var expectedAuthor3 = em.find(Author.class, THIRD_AUTHOR_ID);

        var actualAuthors = repositoryJPA.findAll();

        assertThat(actualAuthors).isNotNull().hasSize(EXPECTED_NUMBER_OF_AUTHORS)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedAuthor1, expectedAuthor2, expectedAuthor3);
    }
}