package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationShellCommands {

    private final StudentService studentService;

    private final TestRunnerService testRunnerService;

    private final LocalizedIOService localizedIOService;

    private Student student;

    @ShellMethod(value = "Login command", key = {"l", "login"})
    public String login(@ShellOption(defaultValue = "anonymous ") String firstName,
                        @ShellOption(defaultValue = "guest") String lastName) {
        student = studentService.determineCurrentStudent();
        return String.format(localizedIOService.getMessage("shell.welcome", student.getFullName()));
    }

    @ShellMethod(value = "Start Test command", key = {"s", "start"})
    @ShellMethodAvailability(value = "isPublishEventCommandAvailable")
    public void start() {
        testRunnerService.run(student);
    }

    private Availability isPublishEventCommandAvailable() {
        return (student == null) ? Availability.unavailable(localizedIOService.getMessage("shell.needLogin"))
                : Availability.available();
    }
}
