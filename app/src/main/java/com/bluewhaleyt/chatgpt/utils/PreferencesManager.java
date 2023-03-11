package com.bluewhaleyt.chatgpt.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.bluewhaleyt.WhaleUtilsApplication;
import com.bluewhaleyt.chatgpt.App;
import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.models.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private SharedPreferences sharedPrefs;

    public static String getChatContext() {
        return getPrefs().getString("message_list", "");
    }

    public static String getOpenAIAPIKey() {
        return getPrefs().getString("pref_openai_api_key", "");
    }

    public static String getOpenAIModel() {
        return getPrefs().getString("pref_openai_model", App.getContext().getString(R.string.default_openai_model));
    }

    public static String getOpenAITemperature() {
        return getPrefs().getString("pref_openai_temperature", "0");
    }

    public static String getOpenAIMaxTokens() {
        return getPrefs().getString("pref_openai_max_tokens", "4000");
    }

    public static boolean isOpenAIEcho() {
        return getPrefs().getBoolean("pref_openai_echo", false);
    }

    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static SharedPreferences getPrefs(String key) {
        return App.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
    }

}
