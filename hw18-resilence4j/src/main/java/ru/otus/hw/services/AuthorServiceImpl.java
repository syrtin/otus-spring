package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final ModelMapper modelMapper;

    @CircuitBreaker(name = "authorServiceFindAll", fallbackMethod = "fallbackFindAllAuthors")
    @Override
    public List<AuthorDto> findAll() {
//        throw new RuntimeException("Simulate error condition");
        var authors = authorRepository.findAll();
        return authors.stream()
                .map(a -> modelMapper.map(a, AuthorDto.class))
                .collect(Collectors.toList());
    }

    public List<AuthorDto> fallbackFindAllAuthors(Exception e) {
        log.error("Failed to retrieve all authors");
        return List.of(new AuthorDto(0, "N/A"));
    }
}