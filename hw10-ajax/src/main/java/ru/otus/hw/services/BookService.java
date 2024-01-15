package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(String id);

    List<BookDto> findAll();

    BookDto insert(String title, String authorId, List<String> genresIds);

    BookDto update(String id, String title, String authorId, List<String> genresIds);

    void deleteById(String id);
}
