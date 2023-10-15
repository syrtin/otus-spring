package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private QuestionConverter questionConverter;

    private TestServiceImpl testService;

    @BeforeEach
    public void setup() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        questionConverter = mock(QuestionConverter.class);
        testService = new TestServiceImpl(ioService, questionDao, questionConverter);
    }

    @Test
    @DisplayName("Check interaction of TestServiceImpl with other services")
    public void testExecuteTest() {
        List<Question> mockQuestions = new ArrayList<>();
        mockQuestions.add(getMockQuestion());

        when(questionDao.findAll()).thenReturn(mockQuestions);
        when(questionConverter.convert(mockQuestions)).thenReturn("Converted questions");

        testService.executeTest();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(questionDao, times(1)).findAll();
        verify(questionConverter, times(1)).convert(mockQuestions);
        verify(ioService, times(1)).printFormattedLine("Converted questions");
    }

    private Question getMockQuestion() {
        return new Question("Test",
                List.of(new Answer("yes", true), new Answer("no", false)));
    }
}