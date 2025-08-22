package me.khun.ybsway.hepler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.YBSWayApplication;

public class LanguageHelper {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANGUAGE = "language";
    private static final Language DEFAULT_LANGUAGE = YBSWayApplication.DEFAULT_LANGUAGE;

    public static void setLanguage(Context context, Language language) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, language.getCode()).apply();
    }

    public static Language getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return Language.ofCode(prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE.getCode()));
    }

    public static Context applyLanguage(Context context, Language language) {
        Locale locale = new Locale(language.getCode());
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    public static Context wrap(Context context) {
        Language language = getLanguage(context);
        return applyLanguage(context, language);
    }
}
