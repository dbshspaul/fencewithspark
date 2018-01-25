package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class RealTimeSiteConfig {


    @Id
    private String _id;

    // Proximity Distance is the in room condition is set to 3.5 meters where the reading from Estimote has the best precision

    private double proximityDistance;
    private double roomDistance;

    // Distance between the Z Axis  Default is 2 meters
    private double zAxisDistance;
    private String siteID;
    private String clientID;
    //Number of Beacons to select from a Axes
    private long selectCount;

    // Has to be set based on location of the beacon
    // Default is 3 meters
    // This is the first Swipe
    private double axisDistanceComp;

    // compares distance between axes in the same floor
    // Default is 2 meters
    private double sameFloorComp;

    private double inroomProximity;

    // Determine Proximity within the axes . It is set to 0.5 meters
    private double axesRoomProximity;

    // Final reduction should be 1.5 meters
    private double axisFinalComp;
    // Will be Ceiling or roomWall
    private String deploymentType;

    // Used Algorithm6 . I represents the room size in meters
    private double secondaryDistance;

    // Used by Algorithm5 . This represets the number of beacons to count
    private int numberOfBeacons;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getProximityDistance() {
        return proximityDistance;
    }

    public void setProximityDistance(double proximityDistance) {
        this.proximityDistance = proximityDistance;
    }

    public double getRoomDistance() {
        return roomDistance;
    }

    public void setRoomDistance(double roomDistance) {
        this.roomDistance = roomDistance;
    }

    public double getzAxisDistance() {
        return zAxisDistance;
    }

    public void setzAxisDistance(double zAxisDistance) {
        this.zAxisDistance = zAxisDistance;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public long getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(long selectCount) {
        this.selectCount = selectCount;
    }

    public double getAxisDistanceComp() {
        return axisDistanceComp;
    }

    public void setAxisDistanceComp(double axisDistanceComp) {
        this.axisDistanceComp = axisDistanceComp;
    }

    public double getSameFloorComp() {
        return sameFloorComp;
    }

    public void setSameFloorComp(double sameFloorComp) {
        this.sameFloorComp = sameFloorComp;
    }

    public double getInroomProximity() {
        return inroomProximity;
    }

    public void setInroomProximity(double inroomProximity) {
        this.inroomProximity = inroomProximity;
    }

    public double getAxesRoomProximity() {
        return axesRoomProximity;
    }

    public void setAxesRoomProximity(double axesRoomProximity) {
        this.axesRoomProximity = axesRoomProximity;
    }

    public double getAxisFinalComp() {
        return axisFinalComp;
    }

    public void setAxisFinalComp(double axisFinalComp) {
        this.axisFinalComp = axisFinalComp;
    }

    public String getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }

    public double getSecondaryDistance() {
        return secondaryDistance;
    }

    public void setSecondaryDistance(double secondaryDistance) {
        this.secondaryDistance = secondaryDistance;
    }

    public int getNumberOfBeacons() {
        return numberOfBeacons;
    }

    public void setNumberOfBeacons(int numberOfBeacons) {
        this.numberOfBeacons = numberOfBeacons;
    }

}

