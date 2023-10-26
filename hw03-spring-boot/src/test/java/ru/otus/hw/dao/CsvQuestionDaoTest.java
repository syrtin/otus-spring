package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CsvQuestionDaoTest {
    private static final String TEST_FILE_NAME_WRONG = "test_en_wrong.csv";

    @MockBean
    private CommandLineRunner commandLineRunner;

    @SpyBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @Test
    @DisplayName("Check of questions extraction runs well")
    public void testFindAllEndsOk() {
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
    public void testFindAllThrowsQuestionReadException() {
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_FILE_NAME_WRONG);
        assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());
    }
}
