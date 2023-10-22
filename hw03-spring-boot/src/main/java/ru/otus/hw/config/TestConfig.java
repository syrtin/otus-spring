package ru.otus.hw.config;

import java.util.Locale;

public interface TestConfig {
    int getRightAnswersCountToPass();

    Locale getLocale();
}
