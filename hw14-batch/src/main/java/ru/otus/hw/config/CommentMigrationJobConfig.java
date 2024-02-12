package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.mapper.BookModelMapper;
import ru.otus.hw.mapper.CommentModelMapper;
import ru.otus.hw.models.h2.Comment;
import ru.otus.hw.models.mongo.CommentDoc;


@SuppressWarnings("unused")
@Configuration
@AllArgsConstructor
public class CommentMigrationJobConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoTemplate mongoTemplate;

    private final CommentModelMapper commentModelMapper;

    private final BookModelMapper bookModelMapper;

    @Bean
    public ItemReader<Comment> commentReader() {
        JpaPagingItemReader<Comment> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select a from Comment a");
        return reader;
    }

    @Bean
    public ItemProcessor<Comment, CommentDoc> commentProcessor() {
        return comment -> {
            var processedComment = commentModelMapper.mapToMongoComment(comment);
            processedComment.setBookDoc(bookModelMapper.mapToMongoBook(comment.getBook()));
            return processedComment;
        };
    }

    @Bean
    public ItemWriter<CommentDoc> mongoCommentWriter() {
        return new MongoItemWriterBuilder<CommentDoc>()
                .collection("comments")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step commentMigrationStep() {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<Comment, CommentDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentReader())
                .processor(commentProcessor())
                .writer(mongoCommentWriter())
                .build();
    }
}
