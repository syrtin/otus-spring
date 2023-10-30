package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final LocalizedIOService localizedIOService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = localizedIOService.readStringWithPromptLocalized("input.firstname");
        var lastName = localizedIOService.readStringWithPromptLocalized("input.lastname");
        return new Student(firstName, lastName);
    }
}
