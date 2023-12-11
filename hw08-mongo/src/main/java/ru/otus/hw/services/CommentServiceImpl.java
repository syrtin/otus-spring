package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getByBookId(String bookId) {
        List<Comment> comments = commentRepository.findAllByBookId(bookId);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto insert(String text, String bookId) {
        Comment comment = save(null, text, bookId);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional
    public CommentDto update(String id, String text, String bookId) {
        Comment comment = save(id, text, bookId);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private Comment save(String id, String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}
