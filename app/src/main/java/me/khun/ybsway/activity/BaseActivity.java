package me.khun.ybsway.activity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import me.khun.ybsway.application.YBSWayApplication;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(YBSWayApplication.languageConfig().wrap(newBase));
    }
}
