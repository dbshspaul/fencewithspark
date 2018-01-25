package com.wrkspot.emp.fence.model.dto;

import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;

import java.util.Date;
import java.util.Map;

/**
 * Created by prakash
 */
//@NodeEntity
@Entity
public class BeaconMetaData implements Comparable {
    // @GraphId
    private Long id;
    private String beaconUUID;
    private double rssi;
    private String properyIdentifer;
    private String axis;
    private long axisPos;
    //Used for corner rooms
    private Map intersections;
    private long floorNumber;
    private String floorWing;
    private String beaconIdentifier;
    private DateTime recordTimeStamp;
    private DateTime postedTime;
    private long calculationRating;
    private long sightingCount;
    private double beaconDistance;
    private long beaconRank;
    private String beaconRating;
    private String frontRoom;
    private String topRoom;
    private String backRoom;
    private String belowRoom;
    private double variance;
    private double standardDeviation;
    private long distanceRank;
    private long signalStrenthRank;
    private long lineOfSightRank;
    private String empId;
    private String siteId;
    private String clientId;
    private Date logTime;
    private String zone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeaconUUID() {
        return beaconUUID;
    }

    public void setBeaconUUID(String beaconUUID) {
        this.beaconUUID = beaconUUID;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String getProperyIdentifer() {
        return properyIdentifer;
    }

    public void setProperyIdentifer(String properyIdentifer) {
        this.properyIdentifer = properyIdentifer;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public long getAxisPos() {
        return axisPos;
    }

    public void setAxisPos(long axisPos) {
        this.axisPos = axisPos;
    }

    public Map getIntersections() {
        return intersections;
    }

    public void setIntersections(Map intersections) {
        this.intersections = intersections;
    }

    public long getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(long floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorWing() {
        return floorWing;
    }

    public void setFloorWing(String floorWing) {
        this.floorWing = floorWing;
    }

    public String getBeaconIdentifier() {
        return beaconIdentifier;
    }

    public void setBeaconIdentifier(String beaconIdentifier) {
        this.beaconIdentifier = beaconIdentifier;
    }

    public DateTime getRecordTimeStamp() {
        return recordTimeStamp;
    }

    public void setRecordTimeStamp(DateTime recordTimeStamp) {
        this.recordTimeStamp = recordTimeStamp;
    }

    public DateTime getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(DateTime postedTime) {
        this.postedTime = postedTime;
    }

    public long getCalculationRating() {
        return calculationRating;
    }

    public void setCalculationRating(long calculationRating) {
        this.calculationRating = calculationRating;
    }

    public long getSightingCount() {
        return sightingCount;
    }

    public void setSightingCount(long sightingCount) {
        this.sightingCount = sightingCount;
    }

    public double getBeaconDistance() {
        return beaconDistance;
    }

    public void setBeaconDistance(double beaconDistance) {
        this.beaconDistance = beaconDistance;
    }

    public long getBeaconRank() {
        return beaconRank;
    }

    public void setBeaconRank(long beaconRank) {
        this.beaconRank = beaconRank;
    }

    public String getBeaconRating() {
        return beaconRating;
    }

    public void setBeaconRating(String beaconRating) {
        this.beaconRating = beaconRating;
    }

    public String getFrontRoom() {
        return frontRoom;
    }

    public void setFrontRoom(String frontRoom) {
        this.frontRoom = frontRoom;
    }

    public String getTopRoom() {
        return topRoom;
    }

    public void setTopRoom(String topRoom) {
        this.topRoom = topRoom;
    }

    public String getBackRoom() {
        return backRoom;
    }

    public void setBackRoom(String backRoom) {
        this.backRoom = backRoom;
    }

    public String getBelowRoom() {
        return belowRoom;
    }

    public void setBelowRoom(String belowRoom) {
        this.belowRoom = belowRoom;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public long getDistanceRank() {
        return distanceRank;
    }

    public void setDistanceRank(long distanceRank) {
        this.distanceRank = distanceRank;
    }

    public long getSignalStrenthRank() {
        return signalStrenthRank;
    }

    public void setSignalStrenthRank(long signalStrenthRank) {
        this.signalStrenthRank = signalStrenthRank;
    }

    public long getLineOfSightRank() {
        return lineOfSightRank;
    }

    public void setLineOfSightRank(long lineOfSightRank) {
        this.lineOfSightRank = lineOfSightRank;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public int compareTo(Object o) {
        BeaconMetaData incommingBeaconInfo = (BeaconMetaData) o;
        return (int) (this.getBeaconRank() - incommingBeaconInfo.getBeaconRank());
    }

}
