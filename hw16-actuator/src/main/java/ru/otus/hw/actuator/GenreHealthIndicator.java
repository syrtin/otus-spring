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
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8080/api/genres";
//        List<Genre> genres = restTemplate.getForObject(url, List.class);
//        org.springframework.web.client.UnknownContentTypeException: Could not extract response:
//        no suitable HttpMessageConverter found for response type [interface java.util.List]
//        and content type [text/html;charset=UTF-8]

        var genres = genreService.findAll();

        if (genres.size() < GENRES_INITIAL_COUNT) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Something wrong with genres or app!")
                    .build();
        } else {
            return Health.up().withDetail("message", "We can get genres, app is fine!")
                    .build();
        }
    }

//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Genre[]> response = restTemplate.exchange("http://localhost:8080/api/genres",
//                HttpMethod.GET, entity, Genre[].class);
//        var genres = List.of(response.getBody());

//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8080/api/genres";
//        ResponseEntity<List<Genre>> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Genre>>() {}
//        );
//        List<Genre> genres = response.getBody();

    //org.springframework.web.client.UnknownContentTypeException: Could not extract response:
    // no suitable HttpMessageConverter found for response type [class [Lru.otus.hw.models.Genre;]
    // and content type [text/html;charset=UTF-8]
}
