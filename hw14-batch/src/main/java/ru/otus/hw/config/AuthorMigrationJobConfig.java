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
import ru.otus.hw.mapper.AuthorModelMapper;
import ru.otus.hw.models.h2.Author;
import ru.otus.hw.models.mongo.AuthorDoc;


@SuppressWarnings("unused")
@Configuration
@AllArgsConstructor
public class AuthorMigrationJobConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoTemplate mongoTemplate;

    private final AuthorModelMapper authorModelMapper;

    @Bean
    public ItemReader<Author> authorReader() {
        JpaPagingItemReader<Author> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select a from Author a");
        return reader;
    }

    @Bean
    public ItemProcessor<Author, AuthorDoc> authorProcessor() {
        return authorModelMapper::mapToMongoAuthor;
    }

    @Bean
    public ItemWriter<AuthorDoc> mongoAuthorWriter() {
        return new MongoItemWriterBuilder<AuthorDoc>()
                .collection("authors")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step authorMigrationStep() {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<Author, AuthorDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader())
                .processor(authorProcessor())
                .writer(mongoAuthorWriter())
                .build();
    }
}
