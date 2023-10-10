package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final char SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try {
            List<QuestionDto> questionDtoList;
            try (InputStreamReader streamReader = new InputStreamReader((getFileFromResourceAsStream(
                    fileNameProvider.getTestFileName())), StandardCharsets.UTF_8)) {
                questionDtoList = new CsvToBeanBuilder<QuestionDto>(streamReader)
                        .withType(QuestionDto.class)
                        .withSeparator(SEPARATOR)
                        .withSkipLines(1)
                        .build()
                        .parse();
            }

            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception ex) {
            throw new QuestionReadException(ex.getMessage(), ex);
        }
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
