package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.h2.Book;
import ru.otus.hw.models.mongo.BookDoc;

@Component
@RequiredArgsConstructor
public class BookModelMapper {

    private final ModelMapper modelMapper;

    public BookDoc mapToMongoBook(Book book) {
        return modelMapper.map(book, BookDoc.class);
    }
}
