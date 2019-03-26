package com.ucll.eventure.Data;

import java.util.HashMap;
import java.util.Map;

public class Friend {

    private String userID;
    private String name;

    public Friend(){}

    public Friend(String userID, String name) {
        this.name = name;
        this.userID = userID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", getUserID());
        result.put("name", getName());

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
