package me.khun.ybsway.activity;



import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import me.khun.ybsway.R;

public class ActivityAbout extends ActivityBase {
    private ActionBar actionBar;
    private TextView aboutBodyTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutBodyTextView = findViewById(R.id.about_body_tv);
        Toolbar toolbar = findViewById(R.id.tool_bar);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //noinspection WrongConstant
            aboutBodyTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}
