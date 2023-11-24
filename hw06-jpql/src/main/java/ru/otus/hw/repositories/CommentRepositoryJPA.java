package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryJPA implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    public CommentRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> getByBookId(long bookId) {
        var sql = "select c from Comment c where c.book.id = :bookId";
        return em.createQuery(sql, Comment.class).setParameter("bookId", bookId).getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        var comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }
}
