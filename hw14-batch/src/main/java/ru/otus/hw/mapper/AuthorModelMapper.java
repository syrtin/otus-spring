package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.h2.Author;
import ru.otus.hw.models.mongo.AuthorDoc;

@Component
@RequiredArgsConstructor
public class AuthorModelMapper {

    private final ModelMapper modelMapper;

    public AuthorDoc mapToMongoAuthor(Author author) {
        return modelMapper.map(author, AuthorDoc.class);
    }
}
