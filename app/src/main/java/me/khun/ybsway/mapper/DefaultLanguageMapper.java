package me.khun.ybsway.mapper;

import static me.khun.ybsway.application.Language.*;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import me.khun.ybsway.R;
import me.khun.ybsway.application.Language;
import me.khun.ybsway.view.LanguageView;

public class DefaultLanguageMapper implements LanguageMapper {

    private final Map<Language, Integer> languageFlagMap = new HashMap<>();

    public DefaultLanguageMapper() {
        languageFlagMap.put(BURMESE, R.drawable.myanmar_flag);
        languageFlagMap.put(ENGLISH, R.drawable.uk_flag);
        languageFlagMap.put(JAPANESE, R.drawable.japan_flag);
    }

    @Override
    public LanguageView mapToLanguageView(Language language) {
        Locale locale = new Locale(language.getCode());
        String englishName = locale.getDisplayLanguage(Locale.ENGLISH);
        String nativeName = locale.getDisplayLanguage(locale);

        LanguageView languageView = new LanguageView();
        languageView.setCode(language.getCode());
        languageView.setNameEn(englishName);
        languageView.setNameNative(nativeName);
        languageView.setIconDrawableId(languageFlagMap.get(language));

        return languageView;
    }

    @Override
    public List<LanguageView> mapToLanguageViewList(List<Language> languageList) {
        return languageList.stream().map(this::mapToLanguageView).collect(Collectors.toList());
    }
}
