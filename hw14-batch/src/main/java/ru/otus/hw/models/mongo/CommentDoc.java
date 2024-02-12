package ru.otus.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class CommentDoc {
    @Id
    private String id;

    private String text;

    @DBRef
    private BookDoc bookDoc;

    public CommentDoc(String text, BookDoc bookDoc) {
        this.text = text;
        this.bookDoc = bookDoc;
    }
}
