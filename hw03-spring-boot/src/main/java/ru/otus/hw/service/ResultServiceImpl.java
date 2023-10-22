package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final IOService ioService;

    private final MessageSource messageSource;

    @Override
    public void showResult(TestResult testResult) {
        ioService.printLine("");
        ioService.printLine(messageSource.getMessage("result.header", null, testConfig.getLocale()));
        ioService.printFormattedLine(messageSource.getMessage("result.student",
                new String[]{testResult.getStudent().getFullName()}, testConfig.getLocale()));
        ioService.printFormattedLine(messageSource.getMessage("result.answeredQuestions",
                new Integer[]{testResult.getAnsweredQuestions().size()}, testConfig.getLocale()));
        ioService.printFormattedLine(messageSource.getMessage("result.rightAnswers",
                new Integer[]{testResult.getRightAnswersCount()}, testConfig.getLocale()));

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            ioService.printLine(messageSource.getMessage("result.congratulations",
                    null, testConfig.getLocale()));
            return;
        }
        ioService.printLine(messageSource.getMessage("result.fail", null, testConfig.getLocale()));
    }
}
