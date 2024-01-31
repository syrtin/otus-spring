package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@Component
public class CommentConverter {
    public String commentDtoToString(CommentDto commentDto) {
        return "Id: %s, Text: %s, BookId: %s"
                .formatted(commentDto.getId(), commentDto.getText(), commentDto.getBook().getId());
    }
}
