package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;

@Repository
public class GenreRepositoryJPA implements GenreRepository {

    @PersistenceContext
    private final EntityManager em;

    public GenreRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Genre> findAll() {
        return em.createQuery("select g from Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(List<Long> ids) {
        var sql = "select g from Genre g where g.id in :ids";
        return em.createQuery(sql, Genre.class).setParameter("ids", ids).getResultList();
    }
}
