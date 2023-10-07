package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final char SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try {
            File questionsFile = getFileFromResource(fileNameProvider.getTestFileName());
            FileReader reader = new FileReader(questionsFile);
            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(SEPARATOR)
                    .withSkipLines(1)
                    .build()
                    .parse();

            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception ex) {
            throw new QuestionReadException(ex.getMessage(), ex);
        }
    }

    private File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found! " + fileName);
        } else {

            return new File(resource.toURI());
        }
    }
}
