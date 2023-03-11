package com.bluewhaleyt.chatgpt.models;

import com.bluewhaleyt.chatgpt.utils.PreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Message {

    private String message;
    private boolean isSentByUser;
    private double sentTime;

    public Message(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
    }

    public Message(String message, boolean isSentByUser, double sentTime) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.sentTime = sentTime;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public boolean isSentByBot() {
        return !isSentByUser;
    }

    public double getSentTime() {
        return sentTime;
    }

    public static List<?> getContext() {
        List<Message> messages;
        var gson = new Gson();
        var json = PreferencesManager.getChatContext();
        var type = new TypeToken<ArrayList<Message>>() {}.getType();
        messages = gson.fromJson(json, type);
        var list = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            list.add(messages.get(i).getMessage());
        }
        return list;
    }

}
