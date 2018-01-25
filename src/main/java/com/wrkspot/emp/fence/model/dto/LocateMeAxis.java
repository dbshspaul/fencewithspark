package com.wrkspot.emp.fence.model.dto;

import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by prakash on 7/12/17.
 * copyrights WRKSPOT Corp
 */
@Entity
public class LocateMeAxis {

    private String axisName;
    private String axisRanking;
    private long confidence;
    private List<UserGrid> locateMes;
    private long axisMatchCount;
    private long finalScore;     // Best Score is 11

    public String getAxisRanking() {
        return axisRanking;
    }

    public void setAxisRanking(String axisRanking) {
        this.axisRanking = axisRanking;
    }

    public long getConfidence() {
        return confidence;
    }

    public void setConfidence(long confidence) {
        this.confidence = confidence;
    }

    public List<UserGrid> getLocateMes() {
        return locateMes;
    }

    public void setLocateMes(List<UserGrid> locateMes) {
        this.locateMes = locateMes;
    }

    public long getAxisMatchCount() {
        return axisMatchCount;
    }

    public void setAxisMatchCount(long axisMatchCount) {
        this.axisMatchCount = axisMatchCount;
    }

    public long getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(long finalScore) {
        this.finalScore = finalScore;
    }

    public String getAxisName() {

        return axisName;
    }

    public void setAxisName(String axisName) {
        this.axisName = axisName;
    }
}
