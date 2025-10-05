package me.khun.ybsway.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.Objects;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.application.YBSWayApplication;

public class ActivityBase extends AppCompatActivity {
    protected InputMethodManager imm;
    private LanguageConfig languageConfig;
    private Language currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    protected void closeSoftInput(IBinder windowToken) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void showSoftInput(View view) {
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected static Bitmap getBitmapFromDrawable(Context context, int drawableId, int widthInDp, int heightInDp) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (drawable == null) return null;

        float density = context.getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * density);
        int heightInPx = (int) (heightInDp * density);

        Bitmap bitmap = Bitmap.createBitmap(widthInPx, heightInPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
