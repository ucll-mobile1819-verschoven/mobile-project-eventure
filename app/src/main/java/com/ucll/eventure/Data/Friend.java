package com.ucll.eventure.Data;

public class Friend {
    private String userID;
    private String name;
    private Boolean accepted;

    public Friend(){}

    public Friend(String userID, String name, Boolean accepted) {
        this.userID = userID;
        this.name = name;
        this.accepted = accepted;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
