package com.bluewhaleyt.chatgpt.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIClient {

    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    private Context context;
    private OkHttpClient client;

    private double requestTime;
    private long readTimeout, writeTimeout, connectTimeout;

    private double maxTokens, temperature;
    private String apiKey , apiUrl, model, prompt;
    private boolean isEcho;

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

    public void setPrompt(String prompt) { this.prompt = prompt; }

    public void setMaxTokens(double maxTokens) { this.maxTokens = maxTokens; }

    public void setTemperature(double temperature) { this.temperature = temperature; }

    public void setEcho(boolean isEcho) { this.isEcho = isEcho; }

    public JSONObject getSettings() throws JSONException {
        var json = new JSONObject();
        json.put("model", model);
        json.put("prompt", prompt);
        json.put("max_tokens", maxTokens);
        json.put("temperature", temperature);
        json.put("echo", isEcho);
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

    public String parseResponse(String responseBody) throws JSONException {
        var jsonObject = new JSONObject(responseBody);
        var jsonArray = jsonObject.getJSONArray("choices");
        var resultText = jsonArray.getJSONObject(0).getString("text");
        return resultText.trim();
    }

    public String parseError(String responseBody) throws JSONException {
        var jsonObject = new JSONObject(responseBody);
        var errorObj = jsonObject.getJSONObject("error");
        var resultText = errorObj.getString("message");
        return resultText;
    }

//    private SpannableStringBuilder getParsedText(String text) {
//        var builder = new SpannableStringBuilder(text);
//        // 定義正則表達式
//        Pattern codeBlockPattern = Pattern.compile("```(\\w+)\n(.*?)\n```", Pattern.DOTALL);
//
//        // 找到所有符合正則表達式的部分
//        Matcher matcher = codeBlockPattern.matcher(builder);
//        while (matcher.find()) {
//            String type = matcher.group(1);
//            String code = matcher.group(2);
//
//            // 將被 ``` 包圍的部分用 SpannableString 包圍起來，同時設置字體樣式
//            int start = matcher.start();
//            int end = matcher.end();
//            var spannableCode = new SpannableString(code + "\n");
//
//            spannableCode.setSpan(new CustomTypefaceSpan(Typeface.MONOSPACE), 0, spannableCode.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.replace(start, end, spannableCode);
//        }
//
//        // 將處理後的文字設置給 TextView
//        return builder;
//    }

}
