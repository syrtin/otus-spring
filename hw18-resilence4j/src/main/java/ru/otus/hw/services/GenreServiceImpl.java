package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    @CircuitBreaker(name = "genreServiceFindAllGenres", fallbackMethod = "fallbackFindAllGenres")
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    @CircuitBreaker(name = "genreServiceCountAllGenres", fallbackMethod = "fallbackCountAllGenres")
    public long countAllGenres() {
        return genreRepository.count();
    }

    public List<Genre> fallbackFindAllGenres(Exception e) {
        log.error("Failed to retrieve all genres");
        return List.of(new Genre(0,"N/A"));
    }

    public long fallbackCountAllGenres(Exception e) {
        return -1;
    }
}