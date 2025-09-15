package me.khun.ybsway.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;
import java.util.Objects;

import me.khun.ybsway.R;
import me.khun.ybsway.view.LanguageView;

public class LanguageListAdapter extends BaseAdapter {
    private final Context context;
    private final List<LanguageView> languageViewList;
    private Integer selectedPosition;

    public LanguageListAdapter(Context context, List<LanguageView> languageList, int selectedPosition) {
        this.context = context;
        this.languageViewList = languageList;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getCount() {
        return languageViewList.size();
    }

    @Override
    public Object getItem(int i) {
        return languageViewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.language_list_item, null);
        }

        LanguageView languageView = languageViewList.get(i);
        ImageView languageIconImageView = view.findViewById(R.id.language_icon_image_view);
        TextView languageNameEnTextView = view.findViewById(R.id.language_name_en);
        TextView languageNameNativeTextView = view.findViewById(R.id.language_name_native);

        languageIconImageView.setImageDrawable(AppCompatResources.getDrawable(context, languageView.getIconDrawableId()));
        languageNameEnTextView.setText(languageView.getNameEn());
        languageNameNativeTextView.setText(languageView.getNameNative());

        if (Objects.equals(selectedPosition, i)) {
            view.setBackgroundResource(R.color.language_list_item_selected);
        } else {
            view.setBackgroundResource(R.color.language_list_item);
        }

        return view;
    }

    public void selectPosition(Integer i) {
        selectedPosition = i;
        notifyDataSetChanged();
    }
}
