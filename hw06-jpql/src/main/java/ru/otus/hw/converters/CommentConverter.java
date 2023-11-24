package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

@Component
public class CommentConverter {
    public String commentToString(Comment comment) {
        return "Id: %d, Text: %s, BookId: %s"
                .formatted(comment.getId(), comment.getText(), comment.getBook().getId());
    }
}
