package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppConfig implements TestConfig, TestFileNameProvider {

    private final int rightAnswersCountToPass;

    private final String testFileName;

    public AppConfig(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                     @Value("${test.fileName}") String testFileName) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
    }

    @Override
    public int getRightAnswersCountToPass() {
        return rightAnswersCountToPass;
    }

    @Override
    public String getTestFileName() {
        return testFileName;
    }
}
