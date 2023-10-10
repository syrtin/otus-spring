package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionConverterImpl implements QuestionConverter {

    @Override
    public String convert(List<Question> questions) {
        return IntStream.range(0, questions.size()).boxed()
                .map(i -> formatQuestion(i + 1, questions.get(i)))
                .collect(Collectors.joining("%n%n"));
    }

    private String formatQuestion(int questionNumber, Question question) {
        return String.format("%d. %s%n%s", questionNumber, question.text(),
                formatAnswers(questionNumber, question.answers()));
    }

    private String formatAnswers(int questionNumber, List<Answer> answers) {
        return IntStream.range(0, answers.size()).boxed()
                .map(i -> String.format("%d.%d. %s", questionNumber, i + 1, answers.get(i).text()))
                .collect(Collectors.joining("%n"));
    }
}
