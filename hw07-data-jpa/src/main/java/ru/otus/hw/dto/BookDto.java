package ru.otus.hw.dto;

import lombok.Data;
import ru.otus.hw.models.Genre;

import java.util.List;

@Data
public class BookDto {
    private long id;

    private String title;

    private AuthorDto author;

    private List<Genre> genres;
}
