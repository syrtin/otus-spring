package ru.otus.hw.services;

import ru.otus.hw.models.User;

import java.util.List;

public interface UserService {

    List<User> findAll();
}
