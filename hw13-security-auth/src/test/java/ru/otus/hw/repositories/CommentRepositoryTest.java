package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataJpaTest
class CommentRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 1;
    private static final long FIRST_BOOK_ID = 1L;
    private static final long FIRST_COMMENT_ID = 1L;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список всех комментариев с информацией о них")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var expectedComment1 = em.find(Comment.class, FIRST_COMMENT_ID);

        var actualComments = repository.findByBookId(FIRST_BOOK_ID);

        assertThat(actualComments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(c -> !c.getText().equals(""))
                .allMatch(c -> !c.getBook().getTitle().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedComment1);
    }
}