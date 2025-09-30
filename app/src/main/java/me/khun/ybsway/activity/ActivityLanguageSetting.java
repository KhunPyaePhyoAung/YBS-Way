package me.khun.ybsway.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import java.util.List;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.LanguageListAdapter;
import me.khun.ybsway.databinding.ActivityLanguageSettingBinding;
import me.khun.ybsway.view.LanguageView;
import me.khun.ybsway.viewmodel.LanguageSettingViewModel;

public class ActivityLanguageSetting extends ActivityBase {
    private ActionBar actionBar;
    private LanguageSettingViewModel languageSettingViewModel;
    private LanguageListAdapter languageListAdapter;
    private ActivityLanguageSettingBinding viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityLanguageSettingBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initViews();
        initObjects();
        initListeners();

        languageSettingViewModel.loadLanguageListData();
    }

    private void initViews() {
        setSupportActionBar(viewBinding.toolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initObjects() {
        LanguageSettingViewModel.Dependencies dependencies = new LanguageSettingViewModel.Dependencies();
        dependencies.languageConfig = YBSWayApplication.languageConfig();
        dependencies.languageMapper = YBSWayApplication.languageMapper;
        languageSettingViewModel = new LanguageSettingViewModel(dependencies);
    }

    private void initListeners() {
        viewBinding.toolBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        viewBinding.languageList.setOnItemClickListener((adapterView, view, i, l) -> {
            onLanguageItemClick(view, i);
        });

        languageSettingViewModel.getLanguageListData().observe(this, this::onLanguageDataLoaded);
        languageSettingViewModel.getSelectedLanguageData().observe(this, this::onSelectedLanguageChanged);
    }

    private void onLanguageItemClick(View view, int position) {
        languageListAdapter.selectPosition(position);
        LanguageView selectedLanguage = (LanguageView) languageListAdapter.getItem(position);
        languageSettingViewModel.setSelectedLanguageData(Language.ofCode(selectedLanguage.getCode()));
    }

    private void onLanguageDataLoaded(List<LanguageView> languageViewList) {
        languageListAdapter = new LanguageListAdapter(this, languageViewList, languageSettingViewModel.getSelectedLanguageIndex());
        viewBinding.languageList.setAdapter(languageListAdapter);
    }

    private void onSelectedLanguageChanged(LanguageView languageView) {
        recreate();
    }
}
