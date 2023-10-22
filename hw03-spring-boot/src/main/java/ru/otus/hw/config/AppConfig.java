package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AppConfig implements TestConfig, TestFileNameProvider {

    private final int rightAnswersCountToPass;

    private final String testFileName;

    private final Locale locale;

    public AppConfig(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                     @Value("${test.fileName}") String testFileName,
                     @Value("${test.locale}") Locale locale) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
        this.locale = locale;
    }

    @Override
    public int getRightAnswersCountToPass() {
        return rightAnswersCountToPass;
    }

    @Override
    public String getTestFileName() {
        return testFileName;
    }


    @Override
    public Locale getLocale() {
        return locale;
    }
}
