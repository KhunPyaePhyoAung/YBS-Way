package me.khun.ybsway.activity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import me.khun.ybsway.hepler.LanguageHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.wrap(newBase));
    }
}
