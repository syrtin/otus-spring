package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataJpaTest
@Import(CommentRepositoryJPA.class)
class CommentRepositoryJPATest {

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 1;
    private static final String NEW_COMMENT_TEXT = "Comment_Text_New";
    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_AUTHOR_ID = 1L;
    private static final long FIRST_COMMENT_ID = 1L;
    private static final long SECOND_COMMENT_ID = 2L;

    @Autowired
    private CommentRepositoryJPA repositoryJPA;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        var optionalActualComment = repositoryJPA.findById(FIRST_COMMENT_ID);
        assertThat(optionalActualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список всех комментариев с информацией о них")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var expectedComment1 = em.find(Comment.class, FIRST_COMMENT_ID);

        var actualComments = repositoryJPA.getByBookId(FIRST_BOOK_ID);

        assertThat(actualComments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(c -> !c.getText().equals(""))
                .allMatch(c -> !c.getBook().getTitle().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedComment1);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var firstBook = em.find(Book.class, FIRST_BOOK_ID);

        Comment expectedComment = new Comment(NEW_COMMENT_TEXT, firstBook);
        var returnedActualComment = repositoryJPA.save(expectedComment);
        assertThat(returnedActualComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        var optionalActualComment = repositoryJPA.findById(returnedActualComment.getId());
        assertThat(optionalActualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var secondBook = em.find(Book.class, SECOND_AUTHOR_ID);

        var returnedComment = repositoryJPA.save(new Comment(FIRST_COMMENT_ID,
                NEW_COMMENT_TEXT, secondBook));

        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);

        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .matches(c -> c.getText().equals(NEW_COMMENT_TEXT))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repositoryJPA.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        var comment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertNotNull(comment);
        assertDoesNotThrow(() -> repositoryJPA.deleteById(FIRST_COMMENT_ID));
        var deletedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertNull(deletedComment);
    }
}