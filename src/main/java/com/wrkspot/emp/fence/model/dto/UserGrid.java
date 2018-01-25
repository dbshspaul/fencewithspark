package com.wrkspot.emp.fence.model.dto;

import org.mongodb.morphia.annotations.Entity;

/**
 * Created by prakash on 7/12/17.
 */
@Entity
public class UserGrid {
    private long accuracy;
    private String location;
    private long confidence;
    private long axisPos;
    private long adjacentConfidence;
    private long finalScore;
    private String propertyIdentifier;
    private long floorNumber;

    public long getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(long accuracy) {
        this.accuracy = accuracy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getConfidence() {
        return confidence;
    }

    public void setConfidence(long confidence) {
        this.confidence = confidence;
    }

    public long getAxisPos() {
        return axisPos;
    }

    public void setAxisPos(long axisPos) {
        this.axisPos = axisPos;
    }

    public long getAdjacentConfidence() {
        return adjacentConfidence;
    }

    public void setAdjacentConfidence(long adjacentConfidence) {
        this.adjacentConfidence = adjacentConfidence;
    }

    public long getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(long finalScore) {
        this.finalScore = finalScore;
    }

    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(String propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public long getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(long floorNumber) {
        this.floorNumber = floorNumber;
    }
}
