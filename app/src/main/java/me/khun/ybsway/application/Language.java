package me.khun.ybsway.application;

import java.util.Objects;

public enum Language {
    BURMESE("my"), ENGLISH("en");

    private final String languageCode;

    Language(String languageCode) {
        this.languageCode = languageCode;
    }

    public static Language ofCode(String languageCode) {
        for (Language lang : values()) {
            if (Objects.equals(languageCode, lang.languageCode)) {
                return lang;
            }
        }
        return null;
    }

    public String getCode() {
        return languageCode;
    }
}
