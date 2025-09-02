package me.khun.ybsway.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageConfig {

    public static final Language DEFAULT_LANGUAGE = Language.BURMESE;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANGUAGE = "language";
    private static LanguageConfig instance;
    private Language currentLanguage;
    private final SharedPreferences prefs;
    private final Context context;

    private LanguageConfig(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentLanguage = Language.ofCode(prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE.getCode()));
    }

    public static synchronized LanguageConfig getInstance(Context context) {
        if (instance == null) {
            instance = new LanguageConfig(context.getApplicationContext());
        }
        return instance;
    }

    public void setLanguage(Language language) {
        prefs.edit().putString(KEY_LANGUAGE, language.getCode()).apply();
        currentLanguage = language;
        applyLanguage(context, language);
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public Context applyLanguage(Context context, Language language) {
        Locale locale = new Locale(language.getCode());
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    public Context wrap(Context context) {
        return applyLanguage(context, getCurrentLanguage());
    }
}
