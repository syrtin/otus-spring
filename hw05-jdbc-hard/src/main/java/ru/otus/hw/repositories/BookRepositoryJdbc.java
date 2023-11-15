package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookRepositoryJdbc implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        var sql = "select b.id, b.title, a.id as author_id, a.full_name, g.id as genre_id, g.name as genre_name " +
                  "from books b " +
                  "join authors a on b.author_id = a.id " +
                  "left join books_genres bg on bg.book_id = b.id " +
                  "left join genres g on g.id = bg.genre_id " +
                  "where b.id = :id";
        var params = Map.of("id", id);

        var books = namedParameterJdbcOperations.query(sql, params, new BookResultSetExtractor());

        if (books.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(books.get(0));
        }
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var params = Map.of("id", id);
        namedParameterJdbcOperations.update("delete from books where id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = "select b.id, b.title, a.id as author_id, a.full_name " +
                  "from books b  join authors a on b.author_id = a.id";

        return namedParameterJdbcOperations.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        RowMapper<BookGenreRelation> rowMapper = (rs, rowNum) -> {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        };
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres", rowMapper);
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var bookMap = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, Function.identity()));

        var genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));

        for (BookGenreRelation relation : relations) {
            long bookId = relation.bookId();
            long genreId = relation.genreId();

            Book book = bookMap.getOrDefault(bookId, null);
            if (book != null) {

                var genre = genreMap.get(genreId);

                if (genre != null) {
                    book.getGenres().add(genre);
                }
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        var params = new MapSqlParameterSource(Map.of("title", book.getTitle(),
                "author_id", book.getAuthor().getId()));
        namedParameterJdbcOperations.update("insert into books (title, author_id) values (:title, :author_id)"
                , params, keyHolder);

        if (keyHolder.getKey() != null) {
            book.setId(keyHolder.getKeyAs(Long.class));
        }
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var params = Map.of("title", book.getTitle(), "author_id", book.getAuthor().getId(), "id", book.getId());
        namedParameterJdbcOperations.update("update books set title = :title, author_id = :author_id where id = :id"
                , params);

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Map<String, Object>> batchParams = new ArrayList<>();
        var genres = book.getGenres();
        var bookId = book.getId();

        for (Genre genre : genres) {
            Map<String, Object> params = Map.of("bookId", bookId, "genreId", genre.getId());
            batchParams.add(params);
        }

        namedParameterJdbcOperations.batchUpdate("insert into books_genres (book_id, genre_id)" +
                " values (:bookId, :genreId)", batchParams.toArray(new Map[0]));
    }

    private void removeGenresRelationsFor(Book book) {
        var params = Map.of("book_id", book.getId());
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :book_id", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = rs.getLong("id");
            var title = rs.getString("title");
            var author = new Author(rs.getLong("author_id"),
                    rs.getString("full_name"));

            return new Book(id, title, author, new ArrayList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {
        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> bookMap = new HashMap<>();
            RowMapper<Book> bookRowMapper = new BookRowMapper();

            while (rs.next()) {
                long bookId = rs.getLong("id");

                Book book = bookMap.get(bookId);
                if (book == null) {
                    book = bookRowMapper.mapRow(rs, 0);
                    bookMap.put(bookId, book);
                }
                book.getGenres().add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
            }

            return new ArrayList<>(bookMap.values());
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}

