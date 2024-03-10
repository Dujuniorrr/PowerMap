package com.ifbaiano.powermap.appearance;

import android.view.View;
import android.widget.TextView;

public class MessageHandlerAppearance {

    private final TextView errorTextView;

    public MessageHandlerAppearance(TextView errorTextView) {
        this.errorTextView = errorTextView;
    }

    public void showErrorTag(String errorMessage) {
        if (errorTextView != null) {
            errorTextView.setText(errorMessage);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    public void clearErrorTag() {
        if (errorTextView != null) {
            errorTextView.setText("");
            errorTextView.setVisibility(View.GONE);
        }
    }
}
