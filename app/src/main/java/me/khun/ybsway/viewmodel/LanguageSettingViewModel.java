package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.mapper.LanguageMapper;
import me.khun.ybsway.view.LanguageView;

public class LanguageSettingViewModel extends ViewModel {

    private final LanguageConfig languageConfig;
    private final LanguageMapper languageMapper;
    private final MutableLiveData<List<LanguageView>> languageListData = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<LanguageView> selectedLanguageData = new MutableLiveData<>();

    public LanguageSettingViewModel(Dependencies dependencies) {
        this.languageConfig = dependencies.languageConfig;
        this.languageMapper = dependencies.languageMapper;
    }

    public LiveData<List<LanguageView>> getLanguageListData() {
        return languageListData;
    }

    public void loadLanguageListData() {
        languageListData.setValue(languageMapper.mapToLanguageViewList(Arrays.asList(Language.values())));
    }

    public int getSelectedLanguageIndex() {
        List<LanguageView> list = languageListData.getValue();
        if (list == null) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getCode(), languageConfig.getCurrentLanguage().getCode())) {
                return i;
            }
        }
        return -1;
    }

    public LiveData<LanguageView> getSelectedLanguageData() {
        return selectedLanguageData;
    }

    public void setSelectedLanguageData(Language language) {
        languageConfig.setLanguage(language);
        selectedLanguageData.setValue(languageMapper.mapToLanguageView(language));
    }

    public static class Dependencies {
        public LanguageConfig languageConfig;
        public LanguageMapper languageMapper;
    }
}
