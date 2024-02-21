package ru.otus.hw.service;

import ru.otus.hw.domain.BoxOffice;
import ru.otus.hw.domain.Movie;

public interface MovieRentalService {


    BoxOffice rentalMovie(Movie movie);
}
