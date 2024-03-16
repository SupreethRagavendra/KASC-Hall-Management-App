package com.kasc.hall.feedbacks;

public class FeedbackData {
    String name, Regno, message;

    public FeedbackData(String name, String Regno, String message) {
        this.name = name;
        this.Regno = Regno;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
