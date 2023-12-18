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
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/books/edit")
    public String showEditForm(@RequestParam("id") String id, Model model) {
        var authors = this.authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = this.genreService.findAll();
        model.addAttribute("genres", genres);

        BookDto book;
        if (id.isEmpty()) {
            book = new BookDto();
        } else {
            Optional<BookDto> bookOptional = bookService.findById(id);
            if (bookOptional.isEmpty()) {
                return "not-found";
            }
            book = bookOptional.get();
        }

        model.addAttribute("book", book);
        return "book-edit";
    }

    @PostMapping("/books/edit")
    public String editBook(@RequestParam("id") String id, @ModelAttribute(value = "book") BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        if (id == null || id.isEmpty()) {
            bookService.insert(title, authorId, genresIds);
        } else {
            bookService.update(id, title, authorId, genresIds);
        }
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
