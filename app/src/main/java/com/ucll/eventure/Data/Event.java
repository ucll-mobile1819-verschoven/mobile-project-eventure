package com.ucll.eventure.Data;

public class Event {
    private String eventID;
    private String eventTitle;
    private String shortDescription;
    private String longDescription;
    private String address;
    private String startTime;
    private String endTime;
    private int attendees;
    private boolean viewBool;

    public Event(){}

    public Event(String eventID, String eventTitle, String shortDescription, String longDescription, String address, String startTime, String endTime, int attendees, boolean viewBool) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendees = attendees;
        this.viewBool = viewBool;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public boolean isViewBool() {
        return viewBool;
    }

    public void setViewBool(boolean viewBool) {
        this.viewBool = viewBool;
    }
}
