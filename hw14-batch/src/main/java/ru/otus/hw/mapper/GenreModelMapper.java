package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.h2.Genre;
import ru.otus.hw.models.mongo.GenreDoc;

@Component
@RequiredArgsConstructor
public class GenreModelMapper {

    private final ModelMapper modelMapper;

    public GenreDoc mapToMongoGenre(Genre genre) {
        return modelMapper.map(genre, GenreDoc.class);
    }
}
