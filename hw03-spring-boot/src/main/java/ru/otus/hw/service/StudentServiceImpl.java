package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    private final MessageSource messageSource;

    private final TestConfig testConfig;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPrompt(messageSource.getMessage("input.firstname", null,
                testConfig.getLocale()));
        var lastName = ioService.readStringWithPrompt(messageSource.getMessage("input.lastname", null,
                testConfig.getLocale()));
        return new Student(firstName, lastName);
    }
}
