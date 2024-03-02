package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "commentServiceFindById", fallbackMethod = "fallbackFindCommentById")
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "commentServiceGetByBookId", fallbackMethod = "fallbackGetCommentByBookId")
    public List<CommentDto> getByBookId(long bookId) {
        List<Comment> comments = commentRepository.findByBookId(bookId);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "commentServiceInsert", fallbackMethod = "fallbackCommentInsert")
    public CommentDto insert(String text, long bookId) {
        Comment comment = save(0, text, bookId);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "commentServiceUpdate", fallbackMethod = "fallbackCommentUpdate")
    public CommentDto update(long id, String text, long bookId) {
        Comment comment = save(id, text, bookId);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "commentServiceDeleteById", fallbackMethod = "fallbackDeleteCommentById")
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }

    public Optional<CommentDto> fallbackFindCommentById(long id, Exception e) {
        log.error("Failed to find comment by id: {}", id);
        return Optional.empty();
    }

    public List<CommentDto> fallbackGetCommentByBookId(long bookId, Exception e) {
        log.error("Failed to find comment by bookId: {}", bookId);
        return Collections.emptyList();
    }

    public CommentDto fallbackCommentInsert(String text, long bookId, Exception e) {
        log.error("Failed to insert new comment {} for bookId {}", text, bookId);
        return new CommentDto();
    }

    public CommentDto fallbackCommentUpdate(long id, String text, long bookId, Exception e) {
        log.error("Failed to update new comment {} for bookId {}", text, bookId);
        return new CommentDto();
    }

    public void fallbackDeleteCommentById(long id, Exception e) {
        log.error("Failed to delete comment with id: {}", id);
    }

}
