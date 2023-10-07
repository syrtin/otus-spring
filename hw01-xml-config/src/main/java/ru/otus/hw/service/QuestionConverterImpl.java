package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

public class QuestionConverterImpl implements QuestionConverter {
    @Override
    public String convert(List<Question> questions) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            result.append(i + 1).append(". ").append(questions.get(i).text()).append("%n");
            List<Answer> answers = questions.get(i).answers();
            for (int j = 0; j < answers.size(); j++) {
                result.append(i + 1).append(".").append(j + 1).append(". ").append(answers.get(j).text()).append("%n");
            }
            result.append("%n");
        }
        return result.toString();
    }
}
