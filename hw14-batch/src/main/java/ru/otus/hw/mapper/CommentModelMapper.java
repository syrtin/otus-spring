package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.h2.Comment;
import ru.otus.hw.models.mongo.CommentDoc;

@Component
@RequiredArgsConstructor
public class CommentModelMapper {

    private final ModelMapper modelMapper;

    public CommentDoc mapToMongoComment(Comment comment) {
        return modelMapper.map(comment, CommentDoc.class);
    }
}
