package ru.otus.hw.services;


import org.springframework.stereotype.Service;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
