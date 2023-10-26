package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class TestServiceImplTest {

    @Autowired
    private TestConfig testConfig;

    @MockBean
    private CommandLineRunner commandLineRunner;

    @MockBean
    private LocalizedIOService localizedIOService;

    @MockBean
    private QuestionDao questionDao;

    @MockBean
    private QuestionConverter questionConverter;

    @Autowired
    private TestServiceImpl testService;

    @Test
    @DisplayName("Check interaction of TestServiceImpl with other services")
    public void testExecuteTestForTest() {
        List<Question> mockQuestions = new ArrayList<>();
        var mockQuestion = getMockQuestion();
        mockQuestions.add(mockQuestion);

        var student = getMockStudent();

        when(questionDao.findAll()).thenReturn(mockQuestions);
        when(questionConverter.convert(mockQuestion)).thenReturn("Converted question");
        when(localizedIOService.readIntForRangeLocalized(1, 2,
                "print.outOfRange")).thenReturn(1);

        testService.executeTestFor(student);

        verify(localizedIOService, times(1)).printLine("");
        verify(localizedIOService, times(1)).printLineLocalized("print.prompt");
        verify(questionDao, times(1)).findAll();
        verify(questionConverter, times(1)).convert(mockQuestion);
        verify(localizedIOService, times(1)).printFormattedLine("Converted question");
        verify(localizedIOService, times(1)).readIntForRangeLocalized(1, 2,
                "print.outOfRange");
    }

    private Question getMockQuestion() {
        return new Question("Test",
                List.of(new Answer("yes", true), new Answer("no", false)));
    }

    private Student getMockStudent() {
        return new Student("Test", "Mock");
    }
}
