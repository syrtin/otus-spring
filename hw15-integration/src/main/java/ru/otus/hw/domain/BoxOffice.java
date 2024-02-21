package ru.otus.hw.domain;

import lombok.Data;

@Data
public class BoxOffice {
    private final Movie movie;

    private final long earnings;
}
