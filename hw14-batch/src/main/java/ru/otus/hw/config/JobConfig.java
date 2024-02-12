package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@SuppressWarnings("unused")
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    public static final String MIGRATION_JOB_NAME = "migrateH2ToMongoJob";

    private final JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    @SuppressWarnings("unused")
    @Bean
    public Job migrateLibraryDatabase(Step authorMigrationStep,
                                      Step genreMigrationStep,
                                      Step bookMigrationStep,
                                      Step commentMigrationStep) {
        return new JobBuilder(MIGRATION_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(authorMigrationStep)
                .next(genreMigrationStep)
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .end()
                .build();
    }
}
