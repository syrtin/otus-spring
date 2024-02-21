package ru.otus.hw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.otus.hw.service.MovieService;

@Slf4j
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		MovieService movieService = ctx.getBean(MovieService.class);
		movieService.startGenerateMoviesLoop();
	}
}
