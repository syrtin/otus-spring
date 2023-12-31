package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Arrays;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    private Author author1;

    private Author author2;

    private Author author3;

    private Book book1;

    private Book book2;

    private Genre genre1;

    private Genre genre2;

    private Genre genre3;

    private Genre genre4;

    private Genre genre5;

    private Genre genre6;

    @ChangeSet(order = "000", id = "dropDB", author = "syrtin", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertAuthors", author = "syrtin", runAlways = true)
    public void insertAuthors(AuthorRepository repository) {
        author1 = repository.save(new Author(ObjectId.get().toString(), "Author_1"));
        author2 = repository.save(new Author(ObjectId.get().toString(), "Author_2"));
        author3 = repository.save(new Author(ObjectId.get().toString(), "Author_3"));
    }

    @ChangeSet(order = "002", id = "insertGenres", author = "syrtin", runAlways = true)
    public void insertGenres(GenreRepository repository) {
        genre1 = repository.save(new Genre(ObjectId.get().toString(), "Genre_1"));
        genre2 = repository.save(new Genre(ObjectId.get().toString(), "Genre_2"));
        genre3 = repository.save(new Genre(ObjectId.get().toString(), "Genre_3"));
        genre4 = repository.save(new Genre(ObjectId.get().toString(), "Genre_4"));
        genre5 = repository.save(new Genre(ObjectId.get().toString(), "Genre_5"));
        genre6 = repository.save(new Genre(ObjectId.get().toString(), "Genre_6"));
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "syrtin", runAlways = true)
    public void insertBooks(BookRepository repository) {
        book1 = repository.save(new Book(ObjectId.get().toString(),
                "BookTitle_1", author1, Arrays.asList(genre1, genre2)));
        book2 = repository.save(new Book(ObjectId.get().toString(),
                "BookTitle_2", author2, Arrays.asList(genre3, genre4)));
        repository.save(new Book(ObjectId.get().toString(), "BookTitle_3", author3, Arrays.asList(genre5, genre6)));
    }

    @ChangeSet(order = "004", id = "insertComments", author = "syrtin", runAlways = true)
    public void insertComments(CommentRepository repository) {
        repository.save(new Comment(ObjectId.get().toString(), "Comment_1", book1));
        repository.save(new Comment(ObjectId.get().toString(), "Comment_2", book2));
        repository.save(new Comment(ObjectId.get().toString(), "Comment_3", book1));
    }
}
