package me.khun.ybsway.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

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
        Configuration overrideConfiguration = new Configuration(newBase.getResources().getConfiguration());

        if (overrideConfiguration.fontScale != 1.0f) {
            overrideConfiguration.fontScale = 1.0f;
        }

        Context context = newBase.createConfigurationContext(overrideConfiguration);
        super.attachBaseContext(YBSWayApplication.languageConfig().wrap(context));
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

    protected static Bitmap getBitmapFromDrawable(Context context, int drawableId, int sizeInDp) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (drawable == null) return null;

        float density = context.getResources().getDisplayMetrics().density;
        int sizeInPx = (int) (sizeInDp * density);

        Bitmap bitmap = Bitmap.createBitmap(sizeInPx, sizeInPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, sizeInPx, sizeInPx);
        drawable.draw(canvas);

        return bitmap;
    }

}
