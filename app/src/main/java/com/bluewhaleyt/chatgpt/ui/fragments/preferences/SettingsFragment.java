package com.bluewhaleyt.chatgpt.ui.fragments.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.utils.PreferencesManager;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;

import java.util.regex.Pattern;

public class SettingsFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey);
        super.onCreatePreferences(savedInstanceState, rootKey);
        init();
    }

    private void init() {
        var prefAPIKey = findPreference("pref_openai_api_key");

        var apiKey = PreferencesManager.getOpenAIAPIKey();
        var pattern = Pattern.compile("(?<=sk-)\\w+");
        var matcher = pattern.matcher(apiKey);
        if (matcher.find()) {
            var match = matcher.group();
            var secret = apiKey.replace(match, "x".repeat(match.length()));
            prefAPIKey.setSummary(secret);
        }

    }

}
