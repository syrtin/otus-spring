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
import ru.otus.hw.mapper.BookModelMapper;
import ru.otus.hw.mapper.GenreModelMapper;
import ru.otus.hw.models.h2.Book;
import ru.otus.hw.models.mongo.BookDoc;

@SuppressWarnings("unused")
@Configuration
@AllArgsConstructor
public class BookMigrationJobConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final EntityManagerFactory entityManagerFactory;

    private final MongoTemplate mongoTemplate;

    private final BookModelMapper bookModelMapper;

    private final AuthorModelMapper authorModelMapper;

    private final GenreModelMapper genreModelMapper;

    @Bean
    public ItemReader<Book> bookReader() {
        JpaPagingItemReader<Book> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select b from Book b");
        return reader;
    }

    @Bean
    public ItemProcessor<Book, BookDoc> bookProcessor() {
        return book -> {
            var processedBook = bookModelMapper.mapToMongoBook(book);
            processedBook.setAuthorDoc(authorModelMapper.mapToMongoAuthor(book.getAuthor()));
            var genres = book.getGenres().stream().map(genreModelMapper::mapToMongoGenre);
            processedBook.setGenreDocs(book.getGenres().stream()
                    .map(genreModelMapper::mapToMongoGenre)
                    .toList());
            return processedBook;
        };
    }

    @Bean
    public ItemWriter<BookDoc> mongoBookWriter() {
        return new MongoItemWriterBuilder<BookDoc>()
                .collection("books")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Step bookMigrationStep() {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<Book, BookDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader())
                .processor(bookProcessor())
                .writer(mongoBookWriter())
                .build();
    }
}
