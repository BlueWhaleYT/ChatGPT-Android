package com.bluewhaleyt.chatgpt.models;

import java.util.Calendar;

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

}
