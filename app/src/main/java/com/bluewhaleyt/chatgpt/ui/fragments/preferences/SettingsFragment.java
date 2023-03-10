package com.bluewhaleyt.chatgpt.ui.fragments.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;

public class SettingsFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey);
        super.onCreatePreferences(savedInstanceState, rootKey);
    }

}
