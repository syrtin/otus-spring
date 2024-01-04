package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.config.MongoConfig;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.changelogsTest.InitMongoDBDataChangeLog.*;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataMongoTest
@Import(MongoConfig.class)
class CommentRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 2;
    private static final int ZERO_NUMBER_OF_COMMENTS = 0;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DisplayName("должен загружать список всех комментариев с информацией о них")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var expectedComment1 = mongoTemplate.findById(FIRST_COMMENT_ID, Comment.class);
        var expectedComment3 = mongoTemplate.findById(THIRD_COMMENT_ID, Comment.class);

        var actualComments = repository.findAllByBookId(FIRST_BOOK_ID);

        assertThat(actualComments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(c -> !c.getText().equals(""))
                .allMatch(c -> !c.getBook().getTitle().equals(""))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedComment1, expectedComment3);
    }

    @DisplayName("должен удалять все комментарии по id книги")
    @Test
    void shouldDeleteAllCommentsByBookId() {
        assertThat(repository.findAllByBookId(FIRST_BOOK_ID)).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS);
        repository.deleteAllByBookId(FIRST_BOOK_ID);
        assertThat(repository.findAllByBookId(FIRST_BOOK_ID)).isNotNull().hasSize(ZERO_NUMBER_OF_COMMENTS);
    }
}