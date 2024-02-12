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
import ru.otus.hw.mapper.GenreModelMapper;
import ru.otus.hw.models.h2.Genre;
import ru.otus.hw.models.mongo.GenreDoc;

@SuppressWarnings("unused")
@Configuration
@AllArgsConstructor
public class GenreMigrationJobConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoTemplate mongoTemplate;

    private final GenreModelMapper genreModelMapper;

    @Bean
    public ItemReader<Genre> genreReader() {
        JpaPagingItemReader<Genre> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select g from Genre g");
        return reader;
    }

    @Bean
    public ItemProcessor<Genre, GenreDoc> genreProcessor() {
        return genreModelMapper::mapToMongoGenre;
    }

    @Bean
    public ItemWriter<GenreDoc> mongoGenreWriter() {
        return new MongoItemWriterBuilder<GenreDoc>()
                .collection("genres")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step genreMigrationStep() {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<Genre, GenreDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(genreReader())
                .processor(genreProcessor())
                .writer(mongoGenreWriter())
                .build();
    }
}
