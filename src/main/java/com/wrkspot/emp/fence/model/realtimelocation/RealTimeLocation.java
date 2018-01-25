package com.wrkspot.emp.fence.model.realtimelocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class RealTimeLocation {
    //  @GraphId
    private Long graphid;


    private String displayTimeStamp;
    private String appliedAlgorithm;
    private DateTime timeStamp;
    private String employeeId;
    private DateTime logTime;
    private long slogTime;
    private long logHour;
    private long logminute;

    private String siteID;
    // Will high, Medium or Low
    private String readingQuality;
    // True will indicate in Room
    // False will indicate not in rom 
    private boolean inRoom;
    // Room and Zone indicates in proximity to many rooms
    private boolean roomAndZone;

    private boolean algorithmError;
    private String algorithmErrorComments;

    private RealTimeRoomInfo realTimeRoomInfo;

    private RealTimeFloorInfo realTimeFloorInfo;

    private List<RealTimeZoneInfo> realTimeZoneInfos;
    private String updateBY;
    private String createdBY;
    private Timestamp creationDate;
    private Timestamp updatedDate;

    private List<RealTimeRoomInfo> determinedRooms;

    private List<RealTimeFloorInfo> determinedFloors;
    //Source Info

    private List<BeaconMetaData> checkinBeacons;

    private List<BeaconMetaData> selectedBeacons;

    private Map<String, List<BeaconMetaData>> axisInfo;

    private List<RealTimeLog> realTimeLogs;

    private String checkIn;

    private List<AxisSelection> axisSelections;

    private List<AxisSelection> finallist;

    private String zoneRooms;

    public Long getGraphid() {
        return graphid;
    }

    public void setGraphid(Long graphid) {
        this.graphid = graphid;
    }

    public String getDisplayTimeStamp() {
        return displayTimeStamp;
    }

    public void setDisplayTimeStamp(String displayTimeStamp) {
        this.displayTimeStamp = displayTimeStamp;
    }

    public String getAppliedAlgorithm() {
        return appliedAlgorithm;
    }

    public void setAppliedAlgorithm(String appliedAlgorithm) {
        this.appliedAlgorithm = appliedAlgorithm;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public DateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(DateTime logTime) {
        this.logTime = logTime;
    }

    public long getSlogTime() {
        return slogTime;
    }

    public void setSlogTime(long slogTime) {
        this.slogTime = slogTime;
    }

    public long getLogHour() {
        return logHour;
    }

    public void setLogHour(long logHour) {
        this.logHour = logHour;
    }

    public long getLogminute() {
        return logminute;
    }

    public void setLogminute(long logminute) {
        this.logminute = logminute;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getReadingQuality() {
        return readingQuality;
    }

    public void setReadingQuality(String readingQuality) {
        this.readingQuality = readingQuality;
    }

    public boolean isInRoom() {
        return inRoom;
    }

    public void setInRoom(boolean inRoom) {
        this.inRoom = inRoom;
    }

    public boolean isRoomAndZone() {
        return roomAndZone;
    }

    public void setRoomAndZone(boolean roomAndZone) {
        this.roomAndZone = roomAndZone;
    }

    public boolean isAlgorithmError() {
        return algorithmError;
    }

    public void setAlgorithmError(boolean algorithmError) {
        this.algorithmError = algorithmError;
    }

    public String getAlgorithmErrorComments() {
        return algorithmErrorComments;
    }

    public void setAlgorithmErrorComments(String algorithmErrorComments) {
        this.algorithmErrorComments = algorithmErrorComments;
    }

    public RealTimeRoomInfo getRealTimeRoomInfo() {
        return realTimeRoomInfo;
    }

    public void setRealTimeRoomInfo(RealTimeRoomInfo realTimeRoomInfo) {
        this.realTimeRoomInfo = realTimeRoomInfo;
    }

    public RealTimeFloorInfo getRealTimeFloorInfo() {
        return realTimeFloorInfo;
    }

    public void setRealTimeFloorInfo(RealTimeFloorInfo realTimeFloorInfo) {
        this.realTimeFloorInfo = realTimeFloorInfo;
    }

    public List<RealTimeZoneInfo> getRealTimeZoneInfos() {
        return realTimeZoneInfos;
    }

    public void setRealTimeZoneInfos(List<RealTimeZoneInfo> realTimeZoneInfos) {
        this.realTimeZoneInfos = realTimeZoneInfos;
    }

    public String getUpdateBY() {
        return updateBY;
    }

    public void setUpdateBY(String updateBY) {
        this.updateBY = updateBY;
    }

    public String getCreatedBY() {
        return createdBY;
    }

    public void setCreatedBY(String createdBY) {
        this.createdBY = createdBY;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public List<RealTimeRoomInfo> getDeterminedRooms() {
        return determinedRooms;
    }

    public void setDeterminedRooms(List<RealTimeRoomInfo> determinedRooms) {
        this.determinedRooms = determinedRooms;
    }

    public List<RealTimeFloorInfo> getDeterminedFloors() {
        return determinedFloors;
    }

    public void setDeterminedFloors(List<RealTimeFloorInfo> determinedFloors) {
        this.determinedFloors = determinedFloors;
    }

    public List<BeaconMetaData> getCheckinBeacons() {
        return checkinBeacons;
    }

    public void setCheckinBeacons(List<BeaconMetaData> checkinBeacons) {
        this.checkinBeacons = checkinBeacons;
    }

    public List<BeaconMetaData> getSelectedBeacons() {
        return selectedBeacons;
    }

    public void setSelectedBeacons(List<BeaconMetaData> selectedBeacons) {
        this.selectedBeacons = selectedBeacons;
    }

    public Map<String, List<BeaconMetaData>> getAxisInfo() {
        return axisInfo;
    }

    public void setAxisInfo(Map<String, List<BeaconMetaData>> axisInfo) {
        this.axisInfo = axisInfo;
    }

    public List<RealTimeLog> getRealTimeLogs() {
        return realTimeLogs;
    }

    public void setRealTimeLogs(List<RealTimeLog> realTimeLogs) {
        this.realTimeLogs = realTimeLogs;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public List<AxisSelection> getAxisSelections() {
        return axisSelections;
    }

    public void setAxisSelections(List<AxisSelection> axisSelections) {
        this.axisSelections = axisSelections;
    }

    public List<AxisSelection> getFinallist() {
        return finallist;
    }

    public void setFinallist(List<AxisSelection> finallist) {
        this.finallist = finallist;
    }

    public String getZoneRooms() {
        return zoneRooms;
    }

    public void setZoneRooms(String zoneRooms) {
        this.zoneRooms = zoneRooms;
    }
}
