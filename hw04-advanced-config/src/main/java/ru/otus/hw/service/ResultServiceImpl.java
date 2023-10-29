package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final LocalizedIOService localizedIOService;

    @Override
    public void showResult(TestResult testResult) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("result.header");
        localizedIOService.printFormattedLineLocalized("result.student",
                testResult.getStudent().getFullName());
        localizedIOService.printFormattedLineLocalized("result.answeredQuestions",
                testResult.getAnsweredQuestions().size());
        localizedIOService.printFormattedLineLocalized("result.rightAnswers",
                testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            localizedIOService.printFormattedLineLocalized("result.congratulations");
            return;
        }
        localizedIOService.printLineLocalized("result.fail");
    }
}
