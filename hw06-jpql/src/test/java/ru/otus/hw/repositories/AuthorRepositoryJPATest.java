package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest
@Import(AuthorRepositoryJPA.class)
class AuthorRepositoryJPATest {

    private static final int EXPECTED_NUMBER_OF_AUTHORS = 3;
    private static final int EXPECTED_QUERIES_COUNT = 1;

    @Autowired
    private AuthorRepositoryJPA repositoryJPA;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        var actualAuthors = repositoryJPA.findAll();

        assertThat(actualAuthors).isNotNull().hasSize(EXPECTED_NUMBER_OF_AUTHORS)
                .allMatch(a -> !a.getFullName().equals(""));

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
    }
}