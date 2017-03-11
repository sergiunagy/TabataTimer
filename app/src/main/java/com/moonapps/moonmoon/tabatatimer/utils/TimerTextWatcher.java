package com.moonapps.moonmoon.tabatatimer.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * class allows actions to be performed on text objects . Can be extended with more object types
 */

public class TimerTextWatcher implements TextWatcher {

    EditText monitoredEditText ;

    public TimerTextWatcher(EditText monitoredEditText) {
        this.monitoredEditText = monitoredEditText;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        monitoredEditText.removeTextChangedListener(this);
        monitoredEditText.setText("");
        monitoredEditText.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        monitoredEditText.setText("");

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
