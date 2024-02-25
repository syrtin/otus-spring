package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findAll();

    BookDto insert(String title, long authorId, List<Long> genresIds);

    BookDto update(long id, String title, long authorId, List<Long> genresIds);

    void deleteById(long id);
}
