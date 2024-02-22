package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
