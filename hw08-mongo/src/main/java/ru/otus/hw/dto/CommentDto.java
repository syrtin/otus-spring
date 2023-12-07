package ru.otus.hw.dto;

import lombok.Data;
import ru.otus.hw.models.Book;

@Data
public class CommentDto {
    private String id;

    private String text;

    private Book book;
}