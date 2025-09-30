package me.khun.ybsway.activity;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import me.khun.ybsway.databinding.ActivityAboutBinding;

public class ActivityAbout extends ActivityBase {
    private ActionBar actionBar;
    private ActivityAboutBinding viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initViews();
        initListeners();
    }

    private void initViews() {
        setSupportActionBar(viewBinding.toolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //noinspection WrongConstant
            viewBinding.aboutBodyTv.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    private void initListeners() {
        viewBinding.toolBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}
