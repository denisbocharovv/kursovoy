package com.example.community;

import android.widget.ImageView;

import java.util.Date;

public class Message {

    public String message_name;
    public String message_text;
    public long message_time;
    public ImageView image_view;

    Message() {}
    Message(String N,String T) {
        this.message_name = N;
        this.message_text = T;

        this.message_time = new Date().getTime();
        this.image_view = null;
    }

    Message(String N,String T, ImageView I) {
        this.message_name = N;
        this.message_text = T;

        this.message_time = new Date().getTime();
        this.image_view = I;
    }

    public String getMessage_name() {
        return message_name;
    }

    public void setMessage_name(String message_name) {
        this.message_name = message_name;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public long getMessage_time() {
        return message_time;
    }

    public void setMessage_time(long message_time) {
        this.message_time = message_time;
    }
}
