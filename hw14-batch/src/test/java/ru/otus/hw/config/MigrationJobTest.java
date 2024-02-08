package ru.otus.hw.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.CommentDoc;
import ru.otus.hw.models.mongo.GenreDoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@SpringBatchTest
class MigrationJobTest {
    private static final String MIGRATION_JOB_NAME = "migrateH2ToMongoJob";

    private static final int EXPECTED_NUMBER_OF_AUTHORS_IN_DB = 3;

    private static final int EXPECTED_NUMBER_OF_GENRES_IN_DB = 6;

    private static final int EXPECTED_NUMBER_OF_BOOKS_IN_DB = 3;

    private static final int EXPECTED_NUMBER_OF_COMMENTS_IN_DB = 2;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void testMigrateLibraryDatabase() throws Exception {

        Job job = jobLauncherTestUtils.getJob();
        Assertions.assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(MIGRATION_JOB_NAME);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        var numberOfAuthorsInMongo = mongoTemplate.count(new Query(), AuthorDoc.class);
        assertEquals(EXPECTED_NUMBER_OF_AUTHORS_IN_DB, numberOfAuthorsInMongo);

        var numberOfGenreInMongo = mongoTemplate.count(new Query(), GenreDoc.class);
        assertEquals(EXPECTED_NUMBER_OF_GENRES_IN_DB, numberOfGenreInMongo);

        var numberOfBooksInMongo = mongoTemplate.count(new Query(), BookDoc.class);
        assertEquals(EXPECTED_NUMBER_OF_BOOKS_IN_DB, numberOfBooksInMongo);

        var numberOfCommentsInMongo = mongoTemplate.count(new Query(), CommentDoc.class);
        assertEquals(EXPECTED_NUMBER_OF_COMMENTS_IN_DB, numberOfCommentsInMongo);
    }
}