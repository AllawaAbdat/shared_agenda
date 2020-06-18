package com.example.monagendapartage.Models.MyEventsModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class MyEventsModel implements Parcelable {

    private String eventId;
    private String startDate;
    private String endDate;
    private String startHour;
    private String endHour;
    private String eventDescription;
    private HashMap<String, String> sharedWith;

    public MyEventsModel() {
    }

    public MyEventsModel(String eventId, String startDate, String endDate, String startHour, String endHour, String eventDescription) {
        this.eventId = eventId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.eventDescription = eventDescription;
    }

    public MyEventsModel(String startDate, String endDate, String startHour, String endHour, String eventDescription, HashMap<String, String> sharedWith) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.eventDescription = eventDescription;
        this.sharedWith = sharedWith;
    }

    protected MyEventsModel(Parcel in) {
        eventId = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        startHour = in.readString();
        endHour = in.readString();
        eventDescription = in.readString();
    }

    public static final Creator<MyEventsModel> CREATOR = new Creator<MyEventsModel>() {
        @Override
        public MyEventsModel createFromParcel(Parcel in) {
            return new MyEventsModel(in);
        }

        @Override
        public MyEventsModel[] newArray(int size) {
            return new MyEventsModel[size];
        }
    };

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public HashMap<String, String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(HashMap<String, String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventId);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeString(startHour);
        parcel.writeString(endHour);
        parcel.writeString(eventDescription);
    }
}
