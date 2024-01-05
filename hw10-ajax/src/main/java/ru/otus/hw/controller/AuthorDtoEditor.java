package ru.otus.hw.controller;

import ru.otus.hw.dto.AuthorDto;

import java.beans.PropertyEditorSupport;

public class AuthorDtoEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(text);
        setValue(authorDto);
    }

    @Override
    public String getAsText() {
        AuthorDto author = (AuthorDto) getValue();
        if (author == null) {
            return "";
        }
        return author.getFullName();
    }
}
