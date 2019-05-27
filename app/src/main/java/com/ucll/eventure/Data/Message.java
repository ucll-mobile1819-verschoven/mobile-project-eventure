package com.ucll.eventure.Data;

public class Message {
    private String sentBy;
    private boolean read;
    private String message;

    public Message(){}

    public Message(String sentBy, boolean read, String message) {
        this.sentBy = sentBy;
        this.read = read;
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
