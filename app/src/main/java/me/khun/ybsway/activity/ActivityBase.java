package me.khun.ybsway.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.application.YBSWayApplication;

public class ActivityBase extends AppCompatActivity {
    private LanguageConfig languageConfig;
    private Language currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageConfig = YBSWayApplication.languageConfig();
        currentLanguage = languageConfig.getCurrentLanguage();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(YBSWayApplication.languageConfig().wrap(newBase));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Language newLanguage = languageConfig.getCurrentLanguage();
        if (!Objects.equals(currentLanguage, newLanguage)) {
            currentLanguage = newLanguage;
            recreate();
        }
    }
}
