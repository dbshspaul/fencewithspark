package com.wrkspot.emp.fence.model.iot;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by murali on 8/21/17.
 */
@Entity
public class AxisSelection {

    private String identifier;
    private String axes;

    private String axisName;

    private String axisFloor;

    private String axisNumber;

    private long lineofSightScore;

    private long proximityScore;


    private int selectionCount;
    /**
     * Is the distance of the closest beacon
     */
    private double axisScore;
    /**
     * Beacons visible to the User on the Axes
     */
    private List<BeaconMetaData> beaconMetaDataList;

    /**
     * Closest beacons based on line of sight and distance
     */
    private List<BeaconMetaData> proximityList;
    /**
     * Line of sight tells us the axes quality. If the beacons of the axes where visible to the user
     * The values will be InLineofSight and NotInLineofSight
     */
    private String lineOfSightStatus;
    /**
     * How close is the person to the beacons on the axes
     */
    private String proximityStatus;
    /**
     * The distance of the closest beacon
     */
    private double axisDistance;
    /**
     * Possible User Locations on the Axes
     */
    private List<Location> zoneLocations;

    private List<Location> finalZoneLocations;

    /**
     * Possible Final Location
     */
    private Location finalLocation;

    /**
     * Zone of the Location
     */
    private Zone zone;

    private long adjacentScore;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAxes() {
        return axes;
    }

    public void setAxes(String axes) {
        this.axes = axes;
    }

    public String getAxisName() {
        return axisName;
    }

    public void setAxisName(String axisName) {
        this.axisName = axisName;
    }

    public String getAxisFloor() {
        return axisFloor;
    }

    public void setAxisFloor(String axisFloor) {
        this.axisFloor = axisFloor;
    }

    public String getAxisNumber() {
        return axisNumber;
    }

    public void setAxisNumber(String axisNumber) {
        this.axisNumber = axisNumber;
    }

    public long getLineofSightScore() {
        return lineofSightScore;
    }

    public void setLineofSightScore(long lineofSightScore) {
        this.lineofSightScore = lineofSightScore;
    }

    public long getProximityScore() {
        return proximityScore;
    }

    public void setProximityScore(long proximityScore) {
        this.proximityScore = proximityScore;
    }

    public int getSelectionCount() {
        return selectionCount;
    }

    public void setSelectionCount(int selectionCount) {
        this.selectionCount = selectionCount;
    }

    public double getAxisScore() {
        return axisScore;
    }

    public void setAxisScore(double axisScore) {
        this.axisScore = axisScore;
    }

    public List<BeaconMetaData> getBeaconMetaDataList() {
        return beaconMetaDataList;
    }

    public void setBeaconMetaDataList(List<BeaconMetaData> beaconMetaDataList) {
        this.beaconMetaDataList = beaconMetaDataList;
    }

    public List<BeaconMetaData> getProximityList() {
        return proximityList;
    }

    public void setProximityList(List<BeaconMetaData> proximityList) {
        this.proximityList = proximityList;
    }

    public String getLineOfSightStatus() {
        return lineOfSightStatus;
    }

    public void setLineOfSightStatus(String lineOfSightStatus) {
        this.lineOfSightStatus = lineOfSightStatus;
    }

    public String getProximityStatus() {
        return proximityStatus;
    }

    public void setProximityStatus(String proximityStatus) {
        this.proximityStatus = proximityStatus;
    }

    public double getAxisDistance() {
        return axisDistance;
    }

    public void setAxisDistance(double axisDistance) {
        this.axisDistance = axisDistance;
    }

    public List<Location> getZoneLocations() {
        return zoneLocations;
    }

    public void setZoneLocations(List<Location> zoneLocations) {
        this.zoneLocations = zoneLocations;
    }

    public List<Location> getFinalZoneLocations() {
        return finalZoneLocations;
    }

    public void setFinalZoneLocations(List<Location> finalZoneLocations) {
        this.finalZoneLocations = finalZoneLocations;
    }

    public Location getFinalLocation() {
        return finalLocation;
    }

    public void setFinalLocation(Location finalLocation) {
        this.finalLocation = finalLocation;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public long getAdjacentScore() {
        return adjacentScore;
    }

    public void setAdjacentScore(long adjacentScore) {
        this.adjacentScore = adjacentScore;
    }
}