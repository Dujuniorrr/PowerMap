package com.ifbaiano.powermap.appearance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.adapter.AdapterCustom;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;

public class CarModelAppearence {
    private final AdapterCustom adapter;
    private final RecyclerView recyclerView;
    private final Context ctx;

    public CarModelAppearence(AdapterCustom adapter, RecyclerView recyclerView, Context ctx) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.ctx = ctx;
    }

    public void restorePreviousClickedItem() {
        int previousClickedIndex = adapter.getPreviousClickedIndex();
        if (previousClickedIndex != -1) {
            View previousClickedView = recyclerView.getChildAt(previousClickedIndex);
            changeTextViewColors(previousClickedView, R.color.black);
            previousClickedView.setBackgroundResource(R.drawable.model_car_item);
        }
    }

    public void setClickedItemColors(View view) {
        view.setBackgroundResource(R.drawable.model_car_selected_item);
        changeTextViewColors(view, R.color.red_custom);
    }

    private void changeTextViewColors(View view, int colorResource) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeTextViewColors(viewGroup.getChildAt(i), colorResource);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(ctx.getResources().getColor(colorResource));
        }
    }

}
