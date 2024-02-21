package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.BoxOffice;
import ru.otus.hw.domain.Movie;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieGateway movieGateway;

    private final MovieCreationService movieCreationService;

    public MovieServiceImpl(MovieGateway movieGateway, MovieCreationService movieCreationService) {
        this.movieGateway = movieGateway;
        this.movieCreationService = movieCreationService;
    }

    @Override
    public void startGenerateMoviesLoop() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int weekNum = i + 1;
            pool.execute(() -> {
                Collection<Movie> movies = movieCreationService.createMovies();
                log.info("For week number {} these movies were created: {}", weekNum,
                        movies.stream().map(Movie::getTitle)
                                .collect(Collectors.joining(", ")));
                Collection<BoxOffice> weeklyBoxOffice = movieGateway.process(movies);
                List<BoxOffice> sortedBoxOffice = weeklyBoxOffice.stream()
                        .sorted(Comparator.comparing(BoxOffice::getEarnings).reversed())
                        .toList();
                log.info("This week we have Movie Rental rating:");
                for (int j = 0; j < sortedBoxOffice.size(); j++) {
                    BoxOffice boxOffice = sortedBoxOffice.get(j);
                    String paybackStatus = boxOffice.getEarnings() > boxOffice.getMovie().getBudget() ?
                            "had a payback" : "hadn't payback";
                    log.info("{}. {} earned {} million rubles and {};", j + 1, boxOffice.getMovie().getTitle(),
                            boxOffice.getEarnings(), paybackStatus);
                }
            });
            delay();
        }
    }

    private void delay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}