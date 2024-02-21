package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.BoxOffice;
import ru.otus.hw.domain.Movie;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieRentalServiceImpl implements MovieRentalService {

    private final Random random = new Random();

    @Override
    public BoxOffice rentalMovie(Movie movie) {
        log.info("Movie {} is running in cinema", movie.getTitle());
        delay();
        log.info("Movie {} no longer running in cinema", movie.getTitle());
        return new BoxOffice(movie, generateRandomRevenue());
    }

    private static void delay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private long generateRandomRevenue() {
        return random.nextInt(500);
    }

}
