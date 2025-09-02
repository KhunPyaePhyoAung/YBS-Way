package me.khun.ybsway.mapper;

import java.util.List;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.view.LanguageView;

public interface LanguageMapper {
    LanguageView mapToLanguageView(Language language);

    List<LanguageView> mapToLanguageViewList(List<Language> languageList);
}
