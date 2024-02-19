package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Movie;
import ru.otus.hw.domain.BoxOffice;

import java.util.Collection;

@MessagingGateway
public interface MovieGateway {
    @Gateway(requestChannel = "movieCreationChannel", replyChannel = "cinemaRentalChannel")
    Collection<BoxOffice> process(Collection<Movie> movies);
}