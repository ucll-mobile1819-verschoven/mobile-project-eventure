package com.ucll.eventure.Data;

import java.util.HashMap;
import java.util.Map;

public class Friend {

    private String userID;
    private String name;
    private Integer eventAmount;

    public Integer getEventAmount() {
        return eventAmount;
    }

    public void setEventAmount(Integer eventAmount) {
        this.eventAmount = eventAmount;
    }

    public Friend(){}

    public Friend(String userID, String name,Integer eventAmount) {
        this.name = name;
        this.userID = userID;
        this.eventAmount = eventAmount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", getUserID());
        result.put("name", getName());
        result.put("amount", getEventAmount());

        return result;
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
}
