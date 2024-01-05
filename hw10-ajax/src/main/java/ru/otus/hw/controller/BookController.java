package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/books/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        List<AuthorDto> authors = this.authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = this.genreService.findAll();
        model.addAttribute("genres", genres);

        Optional<BookDto> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            return "not-found";
        }
        var book = bookOptional.get();

        model.addAttribute("book", book);
        return "book-edit";
    }

    @GetMapping("/books/new")
    public String showNewForm(Model model) {
        var authors = this.authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = this.genreService.findAll();
        model.addAttribute("genres", genres);

        BookDto book = new BookDto();
        model.addAttribute("book", book);
        return "book-new";
    }

    @PostMapping("/books/{id}/edit")
    public String editBook(@PathVariable String id, @ModelAttribute(value = "book") BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        bookService.update(id, title, authorId, genresIds);
        return "redirect:/books";
    }

    @PostMapping("/books/new")
    public String createBook(@ModelAttribute(value = "book") BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        bookService.insert(title, authorId, genresIds);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String getBookById(@PathVariable String id, Model model) {
        var bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            return "not-found";
        }
        model.addAttribute("book", bookOptional.get());
        var comments = commentService.getByBookId(id);
        model.addAttribute("comments", comments);
        return "book-details";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "book-list";
    }

    @DeleteMapping("/books/{id}")
    public String deleteBookById(@PathVariable String id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AuthorDto.class, new AuthorDtoEditor());
    }
}
