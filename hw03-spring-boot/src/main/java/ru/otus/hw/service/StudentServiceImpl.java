package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    private final MessageSource messageSource;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPrompt(messageSource.getMessage("input.firstname", null,
                LocaleContextHolder.getLocale()));
        var lastName = ioService.readStringWithPrompt(messageSource.getMessage("input.lastname", null,
                LocaleContextHolder.getLocale()));
        return new Student(firstName, lastName);
    }
}
