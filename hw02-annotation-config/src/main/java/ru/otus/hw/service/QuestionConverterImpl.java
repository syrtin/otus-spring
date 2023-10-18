package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionConverterImpl implements QuestionConverter {

    @Override
    public String convert(Question question) {
        return formatQuestion(question);
    }

    private String formatQuestion(Question question) {
        return String.format("\t%s\n%s", question.text(),
                formatAnswers(question.answers()));
    }

    private String formatAnswers(List<Answer> answers) {
        return IntStream.range(0, answers.size()).boxed()
                .map(i -> String.format("%d. %s", i + 1, answers.get(i).text()))
                .collect(Collectors.joining("%n"));
    }
}
