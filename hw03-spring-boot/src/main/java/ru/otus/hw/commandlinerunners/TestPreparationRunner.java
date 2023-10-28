package ru.otus.hw.commandlinerunners;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.otus.hw.service.TestRunnerService;

@Service
@RequiredArgsConstructor
public class TestPreparationRunner implements CommandLineRunner {

    private final TestRunnerService testRunnerService;

    @Override
    public void run(String... args) {
        testRunnerService.run();
    }
}
