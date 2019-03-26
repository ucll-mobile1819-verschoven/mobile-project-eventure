package com.ucll.eventure.Data;

import java.util.HashMap;
import java.util.Map;

public class Friends {

    private Map<String, String> friends;

    public Friends(Map<String, String> friends) {
        this.friends = friends;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("friends", getFriends());

        return result;
    }
}
