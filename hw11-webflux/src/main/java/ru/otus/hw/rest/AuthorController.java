package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthorController {

    private final AuthorRepository authorRepository;

    private final ModelMapper modelMapper;

    @GetMapping("/api/authors")
    public Flux<AuthorDto> getAllAuthors() {
        return authorRepository.findAll()
                .map(a -> modelMapper.map(a, AuthorDto.class));
    }
}
