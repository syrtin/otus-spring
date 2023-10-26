package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final QuestionDao questionDao;

    private final QuestionConverter questionConverter;

    private final LocalizedIOService localizedIOService;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("print.prompt");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            localizedIOService.printFormattedLine(questionConverter.convert(question));

            var answerNumber = localizedIOService.readIntForRangeLocalized(1, question.answers().size(),
                    "print.outOfRange");

            var isAnswerValid = question.answers().get(answerNumber - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
