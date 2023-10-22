package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

    @MockBean
    private TestConfig testConfig;

    @MockBean
    private CommandLineRunner commandLineRunner;

    @MockBean
    private IOService ioService;

    @MockBean
    private QuestionDao questionDao;

    @MockBean
    private QuestionConverter questionConverter;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    public void setup() {
        testService = new TestServiceImpl(ioService, questionDao, questionConverter);
    }

    @Test
    @DisplayName("Check interaction of TestServiceImpl with other services")
    public void testExecuteTestForTest() {
        List<Question> mockQuestions = new ArrayList<>();
        var mockQuestion = getMockQuestion();
        mockQuestions.add(mockQuestion);

        var student = getMockStudent();

        when(questionDao.findAll()).thenReturn(mockQuestions);
        when(questionConverter.convert(mockQuestion)).thenReturn("Converted question");
        when(ioService.readIntForRange(1, 2,
                "Your answer is out of range!")).thenReturn(1);

        testService.executeTestFor(student);

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(questionDao, times(1)).findAll();
        verify(questionConverter, times(1)).convert(mockQuestion);
        verify(ioService, times(1)).printFormattedLine("Converted question");
        verify(ioService, times(1)).readIntForRange(1, 2, "Your answer is out of range!");
    }

    private Question getMockQuestion() {
        return new Question("Test",
                List.of(new Answer("yes", true), new Answer("no", false)));
    }

    private Student getMockStudent() {
        return new Student("Test", "Mock");
    }
}
