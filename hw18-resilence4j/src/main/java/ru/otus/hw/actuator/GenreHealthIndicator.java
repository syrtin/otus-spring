package ru.otus.hw.actuator;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.GenreService;

@Component
@AllArgsConstructor
public class GenreHealthIndicator implements HealthIndicator {
    private static final int GENRES_INITIAL_COUNT = 6;

    private final GenreService genreService;

    @Override
    public Health health() {
        var genresCount = genreService.countAllGenres();

        if (genresCount < GENRES_INITIAL_COUNT) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Something wrong with genres or app!")
                    .build();
        } else {
            return Health.up().withDetail("message", "We can get genres, app is fine!")
                    .build();
        }
    }
}
