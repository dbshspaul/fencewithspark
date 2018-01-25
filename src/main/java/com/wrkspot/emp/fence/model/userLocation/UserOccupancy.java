package com.wrkspot.emp.fence.model.userLocation;

import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;

@Entity
public class UserOccupancy {

    private long logTime;

    private List<String> zoneRooms;
    private DateTime sLogTime;
    private long startHour;
    private long startMinute;
    private String siteID;

    private UserEmployee userEmployee;
    private UserProperty userProperty;
    private String checkIn;

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public List<String> getZoneRooms() {
        return zoneRooms;
    }

    public void setZoneRooms(List<String> zoneRooms) {
        this.zoneRooms = zoneRooms;
    }

    public DateTime getsLogTime() {
        return sLogTime;
    }

    public void setsLogTime(DateTime sLogTime) {
        this.sLogTime = sLogTime;
    }

    public long getStartHour() {
        return startHour;
    }

    public void setStartHour(long startHour) {
        this.startHour = startHour;
    }

    public long getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(long startMinute) {
        this.startMinute = startMinute;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public UserEmployee getUserEmployee() {
        return userEmployee;
    }

    public void setUserEmployee(UserEmployee userEmployee) {
        this.userEmployee = userEmployee;
    }

    public UserProperty getUserProperty() {
        return userProperty;
    }

    public void setUserProperty(UserProperty userProperty) {
        this.userProperty = userProperty;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }
}
