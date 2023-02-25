package com.smartdoorlock.iot;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Device {

    private String mName;

    private Boolean mState;
    private Date mTimestamp;

    public Device() {
    } // Needed for Firebase

    public Device(String name, Boolean state) {
        mName = name;
        mState = state;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Boolean getState() {
        return mState;
    }

    public void setState(Boolean state) {
        mState = state;
    }

    @ServerTimestamp
    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

}
