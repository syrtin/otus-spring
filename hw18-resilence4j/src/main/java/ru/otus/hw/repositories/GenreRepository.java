package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Genre;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("select g from Genre g where g.id in :ids")
    List<Genre> findAllByIds(@Param("ids") List<Long> ids);

    long count();
}
