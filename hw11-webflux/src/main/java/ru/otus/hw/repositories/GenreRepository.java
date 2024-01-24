package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

import java.util.List;

@Repository
public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {
    Flux<Genre> findAllByIdIn(List<String> ids);
}