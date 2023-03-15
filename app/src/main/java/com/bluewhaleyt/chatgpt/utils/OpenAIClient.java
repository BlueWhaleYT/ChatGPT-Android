package com.bluewhaleyt.chatgpt.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OpenAIClient {

    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    public static final String TEXT_DAVINCI_003 = "text-davinci-003";
    public static final String GPT_3_5_TURBO = "gpt-3.5-turbo";

    public static final String COMPLETION_URL = "https://api.openai.com/v1/completions";
    public static final String CHAT_COMPLETION_URL = "https://api.openai.com/v1/chat/completions";

    private Context context;
    private OkHttpClient client;

    private double requestTime;
    private long readTimeout, writeTimeout, connectTimeout;

    private double maxTokens, temperature;
    private String apiKey , apiUrl, model, prompt;
    private boolean isMaxTokensEnabled, isEcho;
    private JSONObject parameters;

    public OpenAIClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    long start = System.nanoTime(); // 記錄開始時間
                    var request = chain.request();
                    var response = chain.proceed(request);
                    long end = System.nanoTime(); // 記錄結束時間
                    long duration = end - start; // 計算總時間，單位為納秒
                    // 將總時間轉換為毫秒
                    double millis = duration / 1e6;
                    requestTime = millis;
                    return response;
                })
                .build();
        setTimeout(25000);
    }

    public double getRequestTime() {
        return requestTime;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) { this.writeTimeout = writeTimeout; }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setTimeout(long timeout) {
        setReadTimeout(timeout);
        setWriteTimeout(timeout);
        setConnectTimeout(timeout);
    }

    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public void setModel(String model) { this.model = model; }

    public String getModel() { return model; }

    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getPrompt() { return prompt; }

    public void setMessage(String prompt) { setPrompt(prompt); }

    public void setMaxTokens(double maxTokens) { this.maxTokens = maxTokens; }

    public void setMaxTokensEnabled(boolean isMaxTokensEnabled) { this.isMaxTokensEnabled = isMaxTokensEnabled; }

    public void setTemperature(double temperature) { this.temperature = temperature; }

    public void setEcho(boolean isEcho) { this.isEcho = isEcho; }

    public void setParameters(JSONObject parameters) { this.parameters = parameters; }

    public JSONObject getSettings() throws JSONException {
        var json = new JSONObject();
        json.put("model", model);
        if (isMaxTokensEnabled) json.put("max_tokens", maxTokens);
        json.put("temperature", temperature);

        if (model.equals(TEXT_DAVINCI_003)) {
            json.put("prompt", prompt);
            json.put("echo", isEcho);
        }

        else if (model.equals(GPT_3_5_TURBO)) {
            var jsonArray = new JSONArray();
            var jsonMessageObj = new JSONObject();
            jsonMessageObj.put("role", "user");
            jsonMessageObj.put("content", prompt);
            jsonArray.put(jsonMessageObj);
            json.put("messages", jsonArray);
        }
        return json;
    }

    public void generateResponse(Callback callback) throws JSONException {
        var json = getSettings();
        var request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(json.toString(), mediaType))
                .build();
        client.newCall(request).enqueue(callback);
    }

    public String getResponse(String responseBody) throws JSONException {
        var jsonObject = new JSONObject(responseBody);
        var jsonArray = jsonObject.getJSONArray("choices");
        String resultText = null;
        if (model.equals(TEXT_DAVINCI_003)) {
            resultText = jsonArray.getJSONObject(0).getString("text");
        }

        else if (model.equals(GPT_3_5_TURBO)) {
            var jsonMessageObj = jsonArray.getJSONObject(0).getJSONObject("message");
            resultText = jsonMessageObj.getString("content");
        }

        return resultText.trim();
    }

    public String getErrorResponse(String responseBody) throws JSONException {
        var jsonObject = new JSONObject(responseBody);
        var errorObj = jsonObject.getJSONObject("error");
        var resultText = errorObj.getString("message");
        return resultText;
    }

}
