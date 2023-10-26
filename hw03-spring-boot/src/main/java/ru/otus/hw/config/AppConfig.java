package ru.otus.hw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "test")
public class AppConfig implements TestConfig, TestFileNameProvider, LocaleConfig {

    private int rightAnswersCountToPass;

    private String testFileName;

    private Locale locale;

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

    public void setRightAnswersCountToPass(int rightAnswersCountToPass) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setTestFileName(String testFileName) {
        this.testFileName = testFileName;
    }
}
