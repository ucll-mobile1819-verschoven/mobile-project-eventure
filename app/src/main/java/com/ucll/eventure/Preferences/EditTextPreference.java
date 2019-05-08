package com.ucll.eventure.Preferences;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.ucll.eventure.R;


public class EditTextPreference extends DialogPreference {
    private EditText text;
    private EditTextPreferenceListener listener;

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.pref_dialog_fix);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        text = view.findViewById(R.id.edit);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            listener.onItemClick(this, text.getText().toString());
            // get value from editFields, do whatever you want here :)
            // you can acces them through mView variable very easily
        }
    }

    public void setClickListener(EditTextPreferenceListener listener){
        this.listener = listener;
    }


}
