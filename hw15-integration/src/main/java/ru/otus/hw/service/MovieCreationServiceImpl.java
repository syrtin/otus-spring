package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Genre;
import ru.otus.hw.domain.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieCreationServiceImpl implements MovieCreationService {

    private static final long INITIAL_BUDGET = 1000;

    private final Random random = new Random();

    private final List<String> adjectives = List.of("Синий", "Новый", "Весёлый", "Любовный", "Мощный", "Ночной");

    private final List<String> nouns = List.of("взрыв", "кран", "день", "случай", "электрик", "пёс", "ветер", "сон");


    @Override
    public List<Movie> createMovies() {
        log.info("Developing movies");
        var budgetLeft = INITIAL_BUDGET;
        var movies = new ArrayList<Movie>();
        while (budgetLeft > 0) {
            var movie = createMovie();
            if (budgetLeft < movie.getBudget()) {
                movie.setBudget(budgetLeft);
                log.info("{} has a bad luck, budget has been decreased to {}", movie.getTitle(), movie.getBudget());
            }
            budgetLeft -= movie.getBudget();
            movies.add(movie);
        }
        return movies;
    }

    public Movie createMovie() {
        var title = generateMovieName();
        var budget = generateRandomBudget();
        List<Genre> genres = generateRandomGenres();

        return new Movie(title, genres, budget);
    }

    private String generateMovieName() {
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String noun = nouns.get(random.nextInt(nouns.size()));
        return adjective + " " + noun;
    }

    private long generateRandomBudget() {
        return random.nextInt(491) + 10;
    }

    private List<Genre> generateRandomGenres() {
        int genreCount = random.nextInt(Genre.values().length) + 1;
        List<Genre> allGenres = Arrays.asList(Genre.values());
        return random.ints(0, allGenres.size())
                .distinct()
                .limit(genreCount)
                .mapToObj(allGenres::get)
                .collect(Collectors.toList());
    }

}
