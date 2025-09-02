package me.khun.ybsway.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import me.khun.ybsway.R;
import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.custom.LanguageListAdapter;
import me.khun.ybsway.mapper.LanguageMapper;
import me.khun.ybsway.view.LanguageView;
import me.khun.ybsway.viewmodel.LanguageSettingViewModel;

public class ActivityLanguageSetting extends ActivityBase implements AdapterView.OnItemClickListener {
    private ActionBar actionBar;
    private ListView languageListView;
    private LanguageConfig languageConfig;
    private LanguageMapper languageMapper;
    private LanguageSettingViewModel languageSettingViewModel;
    private LanguageListAdapter languageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);

        languageListView = findViewById(R.id.language_list);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        languageListView.setOnItemClickListener(this);

        languageConfig = YBSWayApplication.languageConfig();
        languageMapper = YBSWayApplication.languageMapper;

        languageSettingViewModel = new LanguageSettingViewModel(languageConfig, languageMapper);

        languageSettingViewModel.getLanguageListData().observe(this, languageViews -> {
            languageListAdapter = new LanguageListAdapter(this, languageViews, languageSettingViewModel.getSelectedLanguageIndex());
            languageListView.setAdapter(languageListAdapter);
        });

        languageSettingViewModel.getSelectedLanguageData().observe(this, selectedLanguage -> {
            recreate();
        });

        languageSettingViewModel.loadLanguageListData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        languageListAdapter.selectPosition(i);
        LanguageView selectedLanguage = (LanguageView) languageListAdapter.getItem(i);
        languageSettingViewModel.setSelectedLanguageData(Language.ofCode(selectedLanguage.getCode()));
    }
}
