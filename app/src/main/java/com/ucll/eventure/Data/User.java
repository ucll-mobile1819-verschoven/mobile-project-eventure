package com.ucll.eventure.Data;
public class User {
    private String databaseID;
    private String name;
    private String email;
    private String messageID;

    public User(){}

    public User(String databaseID, String name, String email, String messageID) {
        this.databaseID = databaseID;
        this.name = name;
        this.email = email;
        this.messageID = messageID;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

}
