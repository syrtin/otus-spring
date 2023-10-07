package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {
    private static final String TEST_FILE_NAME = "test.csv";
    private static final String TEST_FILE_NOT_EXIST_NAME = "test-file-not-exist.csv";

    private TestFileNameProvider fileNameProvider;
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    public void setup() {
        fileNameProvider = mock(TestFileNameProvider.class);
        csvQuestionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    @DisplayName("Check of questions extraction runs well")
    public void testFindAllEndsOk() throws MalformedURLException {
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_FILE_NAME);

        ClassLoader classLoader = mock(ClassLoader.class);
        when(classLoader.getResource(TEST_FILE_NAME)).thenReturn(new File(TEST_FILE_NAME).toURI().toURL());

        List<Question> questions = csvQuestionDao.findAll();

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertEquals(3, questions.size());

        for (Question question : questions) {
            assertNotNull(question.text());
            assertTrue(question.text().length() > 0);
        }

        for (Question question : questions) {
            assertNotNull(question.answers());
            assertTrue(question.answers().size() > 0);
        }

        assertEquals(questions.get(2).text(), "Question three?");
        assertEquals(questions.get(2).answers().get(2).text(), "Maybe");
        assertFalse(questions.get(2).answers().get(2).isCorrect());
    }

    @Test
    @DisplayName("Check of questions extraction ends with QuestionReadException")
    public void testFindAllThrowsQuestionReadException() throws MalformedURLException {
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_FILE_NOT_EXIST_NAME);

        ClassLoader classLoader = mock(ClassLoader.class);
        when(classLoader.getResource(TEST_FILE_NAME)).thenReturn(new File(TEST_FILE_NAME).toURI().toURL());

        assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());
    }
}
