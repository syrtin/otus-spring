package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
@Import(BookRepositoryJPA.class)
class BookRepositoryJPATest {

    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;
    private static final String NEW_BOOK_TITLE = "BookTitle_New";
    private static final long FIRST_BOOK_ID = 1L;
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long SECOND_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;
    private static final long THIRD_GENRE_ID = 3L;
    private static final int EXPECTED_QUERIES_COUNT = 2;

    @Autowired
    private BookRepositoryJPA repositoryJPA;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        var expectedBook = em.find(Book.class, FIRST_BOOK_ID);
        var optionalActualBook = repositoryJPA.findById(FIRST_BOOK_ID);
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBooksList() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        var actualBooks = repositoryJPA.findAll();

        assertThat(actualBooks).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> !b.getTitle().equals(""))
                .allMatch(b -> b.getGenres() != null && b.getGenres().size() > 0)
                .allMatch(b -> !b.getAuthor().getFullName().equals(""));

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var firstAuthor = em.find(Author.class, FIRST_AUTHOR_ID);
        var firstGenre = em.find(Genre.class, FIRST_GENRE_ID);

        Book expectedBook = new Book(NEW_BOOK_TITLE, firstAuthor, List.of(firstGenre));
        var returnedActualBook = repositoryJPA.save(expectedBook);
        assertThat(returnedActualBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        var optionalActualBook = repositoryJPA.findById(returnedActualBook.getId());
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var secondAuthor = em.find(Author.class, SECOND_AUTHOR_ID);
        var thirdGenre = em.find(Genre.class, THIRD_GENRE_ID);

        var returnedBook = repositoryJPA.save(new Book(FIRST_BOOK_ID,
                NEW_BOOK_TITLE, secondAuthor, List.of(thirdGenre)));

        var expectedBook = em.find(Book.class, FIRST_BOOK_ID);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getTitle().equals(NEW_BOOK_TITLE))
                .matches(book -> book.getAuthor().equals(secondAuthor))
                .matches(book -> book.getGenres().get(0).equals(thirdGenre))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(repositoryJPA.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var book = em.find(Book.class, FIRST_BOOK_ID);
        assertNotNull(book);
        assertDoesNotThrow(() -> repositoryJPA.deleteById(FIRST_BOOK_ID));
        var deletedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertNull(deletedBook);
    }
}