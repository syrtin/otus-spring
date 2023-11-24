package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        var comment = commentRepository.findById(id);
        comment.ifPresent(c -> Hibernate.initialize(c.getBook()));
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByBookId(long bookId) {
        List<Comment> comments = commentRepository.getByBookId(bookId);
        for (Comment comment : comments) {
            Hibernate.initialize(comment.getBook());
        }
        return comments;
    }

    @Override
    @Transactional
    public Comment insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Override
    @Transactional
    public Comment update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}
