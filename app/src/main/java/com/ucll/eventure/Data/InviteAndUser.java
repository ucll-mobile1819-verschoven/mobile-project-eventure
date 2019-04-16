package com.ucll.eventure.Data;

public class InviteAndUser {
    private String userID;
    private String eventID;
    private String userName;

    public InviteAndUser(){}

    public InviteAndUser(String userID, String eventID, String userName) {
        this.userID = userID;
        this.eventID = eventID;
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
