package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by murali on 9/1/17.
 */
//@NodeEntity
@Entity
public class RealTimeZoneInfo {
    //  @GraphId
    private Long id;

    private String fromRoomInfo;
    private String toRoomInfo;
    private String floorNo;
    private String zone;
    private long ranking;
    private List<RealTimePlotInfo> plotInfoList;
    private RealTimeZoneRelationship zoneRelationship;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromRoomInfo() {
        return fromRoomInfo;
    }

    public void setFromRoomInfo(String fromRoomInfo) {
        this.fromRoomInfo = fromRoomInfo;
    }

    public String getToRoomInfo() {
        return toRoomInfo;
    }

    public void setToRoomInfo(String toRoomInfo) {
        this.toRoomInfo = toRoomInfo;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public long getRanking() {
        return ranking;
    }

    public void setRanking(long ranking) {
        this.ranking = ranking;
    }

    public List<RealTimePlotInfo> getPlotInfoList() {
        return plotInfoList;
    }

    public void setPlotInfoList(List<RealTimePlotInfo> plotInfoList) {
        this.plotInfoList = plotInfoList;
    }

    public RealTimeZoneRelationship getZoneRelationship() {
        return zoneRelationship;
    }

    public void setZoneRelationship(RealTimeZoneRelationship zoneRelationship) {
        this.zoneRelationship = zoneRelationship;
    }
}
