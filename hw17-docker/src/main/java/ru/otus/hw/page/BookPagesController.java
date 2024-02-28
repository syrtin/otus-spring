package ru.otus.hw.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class BookPagesController {

    @GetMapping("books")
    public String addListPage() {
        return "book-list";
    }

    @GetMapping("books/{id}")
    public String showBookDetails(@PathVariable String id, Model model) {
        model.addAttribute("bookId", id);
        return "book-details";
    }

    @GetMapping("books/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        model.addAttribute("bookId", id);
        return "book-edit";
    }

    @GetMapping("books/new")
    public String showNewForm() {
        return "book-new";
    }
}
