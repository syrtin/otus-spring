package ru.otus.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class BookDoc {

    @Id
    private String id;

    private String title;

    @DBRef
    private AuthorDoc authorDoc;

    @DBRef
    private List<GenreDoc> genreDocs;

    public BookDoc(String title, AuthorDoc authorDoc, List<GenreDoc> genreDocs) {
        this.title = title;
        this.authorDoc = authorDoc;
        this.genreDocs = genreDocs;
    }
}
